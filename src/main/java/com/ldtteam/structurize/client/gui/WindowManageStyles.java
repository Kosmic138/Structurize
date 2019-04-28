package com.ldtteam.structurize.client.gui;

import com.ldtteam.structurize.api.util.Log;
import com.ldtteam.blockout.Loader;
import com.ldtteam.blockout.controls.Button;
import com.ldtteam.blockout.controls.ButtonHandler;
import com.ldtteam.blockout.controls.Image;
import com.ldtteam.blockout.controls.Text;
import com.ldtteam.blockout.views.OverlayView;
import com.ldtteam.blockout.views.Window;
import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.repomanagement.FileManager;
import com.ldtteam.structurize.repomanagement.Styles;
import com.ldtteam.structurize.repomanagement.repostructure.DefaultStyle;
import com.ldtteam.structurize.repomanagement.repostructure.Style;
import net.minecraft.util.Tuple;

import static com.ldtteam.structurize.api.util.constant.Constants.*;
import static com.ldtteam.structurize.api.util.constant.RepositoryConstants.*;
import static com.ldtteam.structurize.api.util.constant.WindowConstants.*;

import java.util.ArrayList;
import java.util.List;

public class WindowManageStyles extends OverlayView implements ButtonHandler {
    /**
     * Resource suffix.
     */
    private static final String WINDOW_XML_PATH = ":gui/windowmanagestyles.xml";

    private String currentStyleId = "";

    private static final Styles styles = Structurize.instance.getStyles();

    private static int MAX_NUM_OF_TABS = 4; // for some reason it can't be final eventhough the init only is in the
                                            // constructor

    private static final String NAME_SELECTED = "_selected";

    private static final List<Tuple<Integer, Integer>> NAME_POSITIONS = new ArrayList<Tuple<Integer, Integer>>();

    public WindowManageStyles(final Window window) {
        super();
        Loader.createFromXMLFile(MOD_ID + WINDOW_XML_PATH, this);
        this.window = window;

        final int styles_size = styles.getStyles().size();
        final int name_pos_y_fix = findPaneOfTypeByID(JSON_NAME + "1", Button.class).getHeight();
        MAX_NUM_OF_TABS = (styles_size < 4) ? styles_size : 4;

        for (int i = 0; i < MAX_NUM_OF_TABS; i++) {
            Button button = findPaneOfTypeByID(JSON_NAME + i, Button.class);
            NAME_POSITIONS.add(new Tuple<Integer, Integer>(button.getX(), button.getY() + name_pos_y_fix));
        }

        if (styles_size > 0) {
            setDisplayStyle(styles.getStyles().get(0));
            adjustTabsBy(1);
        } else {
            findPaneOfTypeByID(JSON_DESCRIPT, Text.class).setTextContent("No style to display!");
        }

        switch (styles_size) {
        case 0:
            findPaneOfTypeByID(JSON_NAME + "0", Button.class).setVisible(false);
        case 1:
            findPaneOfTypeByID(JSON_NAME + "1", Button.class).setVisible(false);
        case 2:
            findPaneOfTypeByID(JSON_NAME + "2", Button.class).setVisible(false);
        case 3:
            findPaneOfTypeByID(JSON_NAME + "3", Button.class).setVisible(false);
            findPaneOfTypeByID(PREVIOUS, Button.class).setVisible(false);
            findPaneOfTypeByID(NEXT, Button.class).setVisible(false);
            return;
        default:
            break;
        }
    }

    /**
     * When a button have been cicked on.
     *
     * @param button which have been clicked on.
     */
    public void onButtonClicked(final Button button) {
        final String id = button.getID();

        // switch style
        if (id.contains(JSON_NAME)) {
            setDisplayStyle(styles.getStyleByName(button.getLabel()));
            return;
        }

        // everything else
        switch (id) {
        case PREVIOUS:
            adjustTabsBy(-1);
            break;
        case NEXT:
            adjustTabsBy(1);
            break;
        case DOWNLOAD:
            FileManager.updateDownloadableStyles(styles, currentStyleId);
            findPaneOfTypeByID(DOWNLOAD, Button.class).setVisible(false);
            break;
        default:
            Log.getLogger().warn("Unset button with ID: " + id);
            break;
        }
    }

    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            setPosition(0, 0);
            setSize(window.getInteriorWidth(), window.getInteriorHeight());
            // Make sure we are on top
            putInside(window);
        } else {
            // Unload dynamic texture
            findPaneOfTypeByID(JSON_IMAGE, Image.class).setImage("minecraft:dirt");
        }
        super.setVisible(visible);
    }

    /**
     * Open the dialog.
     */
    public void open() {
        setVisible(true);
    }

    /**
     * Close the dialog.
     */
    public void close() {
        setVisible(false);
    }

    private void setDisplayStyle(Style style) {
        findPaneOfTypeByID(JSON_NAME, Text.class).setTextContent("§l" + style.getName() + " style");
        findPaneOfTypeByID(JSON_AUTHOR, Text.class).setTextContent("§oby " + style.getAuthor());
        findPaneOfTypeByID(JSON_BASE, Text.class).setTextContent((style instanceof DefaultStyle) ? "Default style"
                : (style.getBase().isEmpty()) ? ""
                        : "Is based on " + styles.getStyleById(style.getBase()).getName() + " style");
        findPaneOfTypeByID(JSON_DESCRIPT, Text.class).setTextContent(style.getDescription());
        findPaneOfTypeByID(JSON_JOKE, Text.class).setTextContent("§o" + style.getJoke());
        findPaneOfTypeByID(JSON_IMAGE, Image.class)
                .setImage(style.getImage().isEmpty() ? "structurize:textures/gui/styles_sample_image.png"
                        : "local:" + style.getImage());
        findPaneOfTypeByID(JSON_REGISTRY, Text.class).setTextContent("TODO: add registry display");
        findPaneOfTypeByID(DOWNLOAD, Button.class).setVisible(true);
        if (!style.getLocalPath().isEmpty()) {
            findPaneOfTypeByID(DOWNLOAD, Button.class).setVisible(false);
        }

        currentStyleId = style.getId();
        adjustTabsBy(0);
    }

    private void adjustTabsBy(int adjust) {
        final List<Style> stylesList = styles.getStyles();
        final Button selected = findPaneOfTypeByID(JSON_NAME + NAME_SELECTED, Button.class);
        int newIndex = stylesList
                .indexOf(styles.getStyleByName(findPaneOfTypeByID(JSON_NAME + "0", Button.class).getLabel())) + adjust;
        selected.setVisible(false);

        while (newIndex < 0) {
            newIndex = stylesList.size() + newIndex;
        }
        while (newIndex >= stylesList.size()) {
            newIndex = newIndex - stylesList.size();
        }

        for (int i = 0; i < MAX_NUM_OF_TABS; i++) {

            if (newIndex + i >= stylesList.size()) {
                newIndex = -i;
            }

            if (stylesList.get(i + newIndex).getId().equals(currentStyleId)) {
                selected.setPosition(NAME_POSITIONS.get(i).getFirst(), NAME_POSITIONS.get(i).getSecond());
                selected.setVisible(true);
            }

            findPaneOfTypeByID(JSON_NAME + i, Button.class).setLabel(stylesList.get(i + newIndex).getShortame());
        }
    }
}