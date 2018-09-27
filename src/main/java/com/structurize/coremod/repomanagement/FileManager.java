package com.structurize.coremod.repomanagement;

import com.structurize.api.configuration.Configurations;
import com.structurize.api.util.Log;
import com.structurize.coremod.Structurize;
import com.structurize.coremod.repomanagement.repostructure.*;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import static com.structurize.api.util.constant.RepositoryConstants.*;

public class FileManager
{
    /**
     * Private constructor to hide implicit public one.
     */
    private FileManager()
    {
        /*
         * Intentionally left empty.
         */
    }
    
    /**
     * Called from init ForgeEvent, loads all information about styles in Configurations.repositoriesUrls
     */
    public static void loadRepositories(final String[] repositories)
    {
        final Styles styles = new Styles();

        for (String url : repositories) {
            final ResourceLocation resourceLoc = new ResourceLocation(url);

            if (resourceLoc.getNamespace().equals(DOWNLOAD_REPO_PREFIX))
            {
                styles.setDownloadFolder(resourceLoc.getPath());
                new File(styles.getDownloadFolder()).mkdirs();
                Log.getLogger().info(styles.getDownloadFolder());
            }

            if (resourceLoc.getNamespace().equals(LOCAL_REPO_PREFIX))
            {
                // TODO DO THIS THING + SCANS
                // TODO Settings.instance
            }

            if (resourceLoc.getNamespace().equals(GITHUB_REPO_PREFIX))
            {
                // get branches
                final String githubUrl = GITHUB_RAW_PREFIX + resourceLoc.getPath() + "/";
                JsonUtils.parseFirstLayerAttribute(NetUtils.httpsGet(githubUrl + GITHUB_URL_MASTER_BRANCHES_JSON), JSON_BRANCHES, new ArrayList<String>()).forEach(branch -> {

                    // get author
                    final String author = JsonUtils.parseFirstLayerAttribute(NetUtils.httpsGet(githubUrl + branch + GITHUB_URL_STYLES_JSON), JSON_AUTHOR, new String());
                    final ResourceLocation resourceLocWithBranch = new ResourceLocation(resourceLoc.getNamespace(), resourceLoc.getPath() + "/" + branch);

                    // get styles
                    JsonUtils.parseFirstLayerAttribute(NetUtils.httpsGet(githubUrl + branch + GITHUB_URL_STYLES_JSON), JSON_STYLE + "s", new ArrayList<String>()).forEach(style -> {
                        Log.getLogger().info(branch + " " + resourceLocWithBranch.toString() + " " + author + " " + style); //debug output here

                        // create new Style and add it to styles
                        final Style newStyle = JsonUtils.parseToStyle(NetUtils.httpsGet(githubUrl + branch + "/" + style + GITHUB_URL_SINGLE_STYLE_JSON), style, resourceLocWithBranch, author);
                        if (newStyle != null)
                        {
                            if (newStyle instanceof DefaultStyle)
                            {
                                newStyle.setLocalPath(styles.getDownloadFolder() + "/" + newStyle.getId());
                                Log.getLogger().info(styles.getDownloadFolder() + "/" + newStyle.getId());
                            }
                            styles.addStyle(newStyle);
                        }
                        else
                        {
                            Log.getLogger().warn("Failed to parse: " + style + ", from branch: " + branch);
                        }
                    });
                });
            }
        }

        doRegistry(styles);

        updateDownloadableStyles(styles);
    }

    /**
     * Fills registry into non-default styles, also fills bases, ---> must always end with saving Styles <---
     */
    private static void doRegistry(final Styles styles)
    {
        // bases
        
        for(int i = 0; i < styles.getStyles().size(); i++)
        {
            final Style style = styles.getStyles().get(i);
            if (!(style instanceof DefaultStyle) && !style.getBase().isEmpty())
            {
                final Style base = styles.getStyles().stream().filter(styleB -> styleB.getId().equals(style.getBase())).findFirst().orElse(null);

                if (base == null)
                {
                    Log.getLogger().warn("Base with id \"" + style.getBase() + "\" was not found!");
                    return;
                }

                for(int j = 0; j < base.getHeads().size(); j++)
                {
                    final Head headBase = base.getHeads().get(j);
                    final Head headToCheck = style.getHeadById(headBase.getId());

                    if (headToCheck == null)
                    {
                        style.addHead(headBase);
                    }
                    else
                    {
                        for(int k = 0; k < headBase.getTypes().size(); k++)
                        {
                            final Type typeBase = headBase.getTypes().get(k);
                            final Type typeToCheck = headToCheck.getTypeById(typeBase.getId());

                            if (typeToCheck == null)
                            {
                                headToCheck.addType(typeBase);
                            }
                            else
                            {
                                for(int l = 0; l < typeBase.getFiles().size(); l++)
                                {
                                    final Trinuple<String, String, String> fileBase = typeBase.getFiles().get(l);
                                    final Trinuple<String, String, String> fileToCheck = typeToCheck.getFileById(fileBase.getFirst());

                                    if (fileToCheck == null)
                                    {
                                        typeToCheck.addFile(fileBase);
                                    }
                                }

                                headToCheck.setType(k, typeToCheck);
                            }
                        }

                        style.setHead(j, headToCheck);
                    }
                }

                styles.setStyle(i, style);
            }
        }
        
        final DefaultStyle defaultstyle = (DefaultStyle) styles.getStyles().stream().filter(style -> style instanceof DefaultStyle).findFirst().orElse(null);

        if (defaultstyle == null)
        {
            Log.getLogger().info("No default style found, can't build registry (not an error)");
            Structurize.instance.setStyles(styles);
            return;
        }

        // registry
        for(int i = 0; i < styles.getStyles().size(); i++)
        {
            final Style style = styles.getStyles().get(i);
            if (!(style instanceof DefaultStyle))
            {
                for(int j = 0; j < defaultstyle.getRegistry().size(); j++)
                {
                    final String head = defaultstyle.getRegistry().get(j);
                          Head headToCheck = style.getHeadById(head);
                    final DefaultHead headRegistry = (DefaultHead) defaultstyle.getHeadById(head);

                    if (headToCheck != null && headToCheck.getName().isEmpty())
                    {
                        headToCheck.setName(headRegistry.getName());
                    }

                    if (headToCheck == null)
                    {
                        headToCheck = (Head) headRegistry;
                        for(int k = 0; k < headToCheck.getTypes().size(); k++)
                        {
                            final Type typeToCheck = headToCheck.getTypes().get(k);
                            final DefaultType typeRegistry = (DefaultType) headRegistry.getTypeById(headRegistry.getRegistry().get(k));

                            typeToCheck.setFileExtension(typeRegistry.getFileExtension());
                            typeToCheck.setOrigin(style.getId());
                            headToCheck.setType(k, typeToCheck);
                        }
                    }
                    else
                    {
                        for(int k = 0; k < headRegistry.getRegistry().size(); k++)
                        {
                            final String type = headRegistry.getRegistry().get(k);
                            final Type typeToCheck = headToCheck.getTypeById(type);
                            final DefaultType typeRegistry = (DefaultType) headRegistry.getTypeById(type);

                            if (typeToCheck == null)
                            {
                                headToCheck.addType((Type) typeRegistry);
                            }
                            else
                            {
                                typeToCheck.setFileExtension(typeRegistry.getFileExtension());
                                typeToCheck.setOrigin(style.getId());
                                if (typeToCheck.getName().isEmpty())
                                {
                                    typeToCheck.setName(typeRegistry.getName());
                                }

                                for(int l = 0; l < typeRegistry.getRegistry().size(); l++)
                                {
                                    final String file = headRegistry.getRegistry().get(l);
                                    final Trinuple<String, String, String> fileToCheck = typeToCheck.getFileById(file);
                                    final Trinuple<String, String, String> fileRegistry = typeRegistry.getFileById(file);

                                    if (fileToCheck == null)
                                    {
                                        typeToCheck.addFile(fileRegistry);
                                    }

                                    if (fileToCheck != null && fileToCheck.getSecond().isEmpty())
                                    {
                                        fileToCheck.setSecond(typeRegistry.getName());
                                        typeToCheck.setFile(l, fileToCheck);
                                    }
                                }

                                headToCheck.setType(k, typeToCheck);
                            }
                        }
                    }

                    style.setHead(j, headToCheck);
                }

                styles.setStyle(i, style);
            }
        }

        Structurize.instance.setStyles(styles);
    }
    
    public static void updateDownloadableStyles(final Styles styles, final String styleToDownload)
    {
        final Style style = styles.getStyleById(styleToDownload);
        styles.getStyleById(styleToDownload).setLocalPath(styles.getDownloadFolder() + "/" + style.getId());
        if (!style.getBase().isEmpty())
        {
            styles.getStyleById(style.getBase()).setLocalPath(styles.getDownloadFolder() + "/" + style.getBase());
        }
        Structurize.instance.setStyles(styles);
        updateDownloadableStyles(styles);
    }

    /**
     * Update all downloaded styles which are also in styles
     * Also downloads the default style if already not downloaded
     */
    private static void updateDownloadableStyles(final Styles styles)
    {
        List<Tuple<String, String>> latestCommits = readLocalBranches(styles);

        styles.getStyles().forEach(style -> {
            if (!style.getLocalPath().isEmpty() && style.getOrigin().getNamespace().equals(GITHUB_REPO_PREFIX))
            {
                // See GitHub Api there, it's a magic, good luck with understanding this :D
                final Tuple<String, String> localLatestSha = latestCommits.stream().filter(localStyle -> localStyle.getFirst().equals(style.getId())).findFirst().orElse(null);
                final String styleOrigin = style.getOrigin().getPath();
                final String url0 = JsonUtils.parseSecondLayerAttribute(NetUtils.httpsGet(GITHUB_API_REPOS_PREFIX + styleOrigin.substring(0, styleOrigin.lastIndexOf("/")) + GITHUB_URL_REFS_HEADS + styleOrigin.substring(styleOrigin.lastIndexOf("/"))),
                    GITHUB_COMMITJSON_OBJECT, GITHUB_COMMITJSON_URL, new String());
                final String url1 = JsonUtils.parseSecondLayerAttribute(NetUtils.httpsGet(url0), GITHUB_COMMITJSON_TREE, GITHUB_COMMITJSON_URL, new String());
                final String styleLatestSha = JsonUtils.parseFirstLayerObjectArrayToTuple(NetUtils.httpsGet(url1), GITHUB_COMMITJSON_TREE, GITHUB_COMMITJSON_PATH, GITHUB_COMMITJSON_SHA)
                    .stream()
                    .filter(styleId -> style.getId().equals(styleId.getFirst()))
                    .findFirst()
                    .get().getSecond();
                Log.getLogger().debug(style.getId() + ":" + styleLatestSha);
                
                if (localLatestSha == null || !localLatestSha.getSecond().equals(styleLatestSha))
                {
                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> downloadStyle(style));
                    if (localLatestSha == null)
                    {
                        latestCommits.add(new Tuple<String, String>(style.getId(), styleLatestSha));
                    }
                    else
                    {
                        latestCommits.set(latestCommits.indexOf(localLatestSha), new Tuple<String, String>(style.getId(), styleLatestSha));
                    }
                }
            }
        });

        saveLocalBranches(latestCommits, styles);
    }

    private static void downloadStyle(final Style style)
    {
        Log.getLogger().info("Download started: " + style.getId());
        new File(style.getLocalPath()).mkdirs();
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Configurations.amountOfThreadsForDownloading);

        style.getHeads().forEach(head -> {
            head.setLocalPath(style.getLocalPath() + "/" + head.getId());
            new File(head.getLocalPath()).mkdirs();

            head.getTypes().forEach(type -> {
                type.setLocalPath(head.getLocalPath() + "/" + type.getId());
                new File(type.getLocalPath()).mkdirs();

                type.getFiles().forEach(file -> {
                    if (file.getThird().equals(type.getOrigin()))
                    {
                        //https://github.com/ldtteam/minecolonies-schematics/raw/generic/default-wooden/buildings/baker/baker1.nbt
                        final String styleOrigin = style.getOrigin().getPath();
                        final String url = GITHUB_URL_PREFIX + styleOrigin.substring(0, styleOrigin.lastIndexOf("/")) + GITHUB_URL_RAW_WORD + styleOrigin.substring(styleOrigin.lastIndexOf("/")) + "/" + style.getId() + "/" + head.getId() + "/" + type.getId() + "/" + file.getFirst() + type.getFileExtension();
                        executor.submit(() -> NetUtils.schematicHttpsGet(url , type.getFilePathById(file.getFirst())));
                    }
                });
            });
        });

        while (!executor.getQueue().isEmpty() || executor.getActiveCount() > 0)
        {
            // Wait for all tasks to end
        }
        Log.getLogger().info("Download finished: " + style.getId());
    }

    private static List<Tuple<String, String>> readLocalBranches(final Styles styles)
    {
        final String path = styles.getDownloadFolder() + "/" + JSON_BRANCHES + JSON_SUFFIX;

        if (!new File(path).exists())
        {
            try
            {
                new File(path).createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            saveLocalBranches(new ArrayList<Tuple<String, String>>(), styles);
        }
        return JsonUtils.parseFirstLayerObjectArrayToTuple(readFileToString(path), JSON_BRANCHES, "a", "b");
    }

    private static void saveLocalBranches(final List<Tuple<String, String>> branches, final Styles styles)
    {
        final String path = styles.getDownloadFolder() + "/" + JSON_BRANCHES + JSON_SUFFIX;

        try (final PrintWriter file = new PrintWriter(path))
        {
            file.print("{\"branches\":" + JsonUtils.stringifyToJson(branches) + "}");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static String readFileToString(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append(" "));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}