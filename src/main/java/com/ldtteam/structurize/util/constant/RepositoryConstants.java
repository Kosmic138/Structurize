package com.structurize.api.util.constant;

import org.jetbrains.annotations.NonNls;

/**
 * Constats used by <code>com.structurize.coremod.repomanagement</code>
 */
public final class RepositoryConstants
{
    // JSON file names
    @NonNls
    public static final String JSON_SUFFIX   = ".json";
    @NonNls
    public static final String JSON_BRANCHES = "branches";
    @NonNls
    public static final String JSON_STYLE    = "style";
    
    // JSON attributes
    @NonNls
    public static final String JSON_AUTHOR    = "author";
    @NonNls
    public static final String JSON_NAME      = "name";
    @NonNls
    public static final String JSON_BASE      = "base";
    @NonNls
    public static final String JSON_DESCRIPT  = "description";
    @NonNls
    public static final String JSON_DEFAULT   = "default";
    @NonNls
    public static final String JSON_FILE_EXT  = "file-type";
    @NonNls
    public static final String JSON_IMAGE     = "image";
    @NonNls
    public static final String JSON_JOKE      = "joke";
    @NonNls
    public static final String JSON_REGISTRY  = "registry";
    @NonNls
    public static final String JSON_HEADS     = "heads";
    @NonNls
    public static final String JSON_TYPES     = "types";
    @NonNls
    public static final String JSON_FILES     = "files";
    @NonNls
    public static final String JSON_ANY       = "any";
    @NonNls
    public static final String JSON_NAMES     = "names";
    @NonNls
    public static final String JSON_SHORTNAME = "shortname";

    // Repository prefixes aka namespaces
    @NonNls
    public static final String DOWNLOAD_REPO_PREFIX = "download";
    @NonNls
    public static final String LOCAL_REPO_PREFIX    = "local";
    @NonNls
    public static final String GITHUB_REPO_PREFIX   = "github";

    // Github things
    @NonNls
    public static final String GITHUB_RAW_PREFIX               = "https://raw.githubusercontent.com/";
    @NonNls
    public static final String GITHUB_API_REPOS_PREFIX         = "https://api.github.com/repos/";
    @NonNls
    public static final String GITHUB_URL_PREFIX               = "https://github.com/";
    @NonNls
    public static final String GITHUB_URL_RAW_WORD              = "/raw";
    @NonNls
    public static final String GITHUB_URL_MASTER_BRANCHES_JSON = "master/" + JSON_BRANCHES + JSON_SUFFIX;
    @NonNls
    public static final String GITHUB_URL_STYLES_JSON          = "/" + JSON_STYLE + "s" + JSON_SUFFIX;
    @NonNls
    public static final String GITHUB_URL_SINGLE_STYLE_JSON    = "/" + JSON_STYLE + JSON_SUFFIX;
    @NonNls
    public static final String GITHUB_URL_REFS_HEADS           = "/git/refs/heads";
    @NonNls
    public static final String GITHUB_COMMITJSON_SHA           = "sha";
    @NonNls
    public static final String GITHUB_COMMITJSON_URL           = "url";
    @NonNls
    public static final String GITHUB_COMMITJSON_OBJECT        = "object";
    @NonNls
    public static final String GITHUB_COMMITJSON_TREE          = "tree";
    @NonNls
    public static final String GITHUB_COMMITJSON_PATH          = "path";

    /**
     * Private constructor to hide implicit public one.
     */
    private RepositoryConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}
