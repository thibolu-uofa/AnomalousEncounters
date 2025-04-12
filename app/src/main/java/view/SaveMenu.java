package view;

import static view.ViewConstants.BLUE_TEXT_COLOR;
import static view.ViewConstants.GREEN_TEXT_COLOR;
import static view.ViewConstants.PURPLE_TEXT_COLOR;
import static view.ViewConstants.RED_TEXT_COLOR;
import static view.ViewConstants.SCREEN_WIDTH;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.anomalousencounters.R;

import java.util.ArrayList;
import java.util.Objects;

import presenter.GamePresenter;
import view.menu.BaseMenu;
import view.menu.MenuItem;

public class SaveMenu extends BaseMenu {
    private GameView gameView;
    private MenuItem overwriteButton;
    private MenuItem newSaveSlotButton;
    private MenuItem loadSaveButton;
    private MenuItem deleteSaveButton;
    private  MenuItem saveInfoButton;
    private RadioBtnList saveRadioBtnList;
    private String selectedSaveSlot;
    private final int Y = 150;
    private final int MENU_HEADING_HEIGHT = 90;
    private final int MENU_HEIGHT = 300;
    private final int MENU_WIDTH = 750;
    private final int BTN_WIDTH = 350;
    private final String BLANK_TEXT = "";
    private String newSaveMsg, overwriteMsg, deleteMsg, loadMsg, loadSaveMsg;

    public SaveMenu(GamePresenter presenter, Context context, GameView gameView){
        super(presenter, context);
        this.gameView = gameView;
        int x = SCREEN_WIDTH/2 - MENU_WIDTH/2;

        createSaveMenu(x);
        createSaveButton(x);
        createLoadButton(x);
        createOverwriteButton(x);
        createDeleteButton(x);
        createInfoButton(x);
    }

    private void createSaveMenu(int x) {
        String SAVE_HEADING = "SAVE SLOTS";

        MenuItem save_heading = new MenuItem(x, Y, MENU_HEADING_HEIGHT, MENU_WIDTH, SAVE_HEADING, true, context);
        menuItemsList.put("save_heading", save_heading);

        MenuItem saveSlots = new MenuItem(x, Y + MENU_HEADING_HEIGHT, MENU_HEIGHT, MENU_WIDTH, BLANK_TEXT, false, context);
        menuItemsList.put("save_slots", saveSlots);

        createSaveSlotList(x);
    }

    private void createSaveSlotList(int x) {
        ArrayList<String> saveArrayList = presenter.getSaveSlotList();
        int RADIO_X_PAD = 15;
        int RADIO_Y_PAD = 20;
        Rectangle saveSlotListRect = new Rectangle(x + RADIO_X_PAD, Y + MENU_HEADING_HEIGHT + RADIO_Y_PAD);
        saveRadioBtnList = new RadioBtnList(saveSlotListRect.x, saveSlotListRect.y, context, saveArrayList, MENU_WIDTH, MENU_HEIGHT);
    }

    private void createSaveButton(int x) {
        String SAVE_TEXT = "New Save";
        int BUTTON_Y_PAD = 50;
        int SAVE_BTN_Y = Y + MENU_HEADING_HEIGHT + MENU_HEIGHT + BUTTON_Y_PAD;

        newSaveSlotButton = new MenuItem(x, SAVE_BTN_Y, MENU_HEADING_HEIGHT, BTN_WIDTH, SAVE_TEXT,true, context);
        newSaveSlotButton.changeFontColor(GREEN_TEXT_COLOR);
    }

    private void createLoadButton(int x) {
        String TEXT = "Load Save";
        int BUTTON_Y_PAD = 50;
        int SAVE_BTN_Y = Y + MENU_HEADING_HEIGHT + MENU_HEIGHT + BUTTON_Y_PAD + MENU_HEADING_HEIGHT + BUTTON_Y_PAD;

        loadSaveButton = new MenuItem(x, SAVE_BTN_Y, MENU_HEADING_HEIGHT, BTN_WIDTH, TEXT,true, context);
        loadSaveButton.changeFontColor(BLUE_TEXT_COLOR);
    }

    private void createOverwriteButton(int x) {
        String OVERWRITE_TEXT = "Overwrite";
        int PAD = 50;
        int OVERWRITE_BTN_X = x + BTN_WIDTH + PAD;
        int OVERWRITE_BTN_Y = Y + MENU_HEADING_HEIGHT + MENU_HEIGHT + PAD;

        overwriteButton = new MenuItem(OVERWRITE_BTN_X, OVERWRITE_BTN_Y, MENU_HEADING_HEIGHT, BTN_WIDTH, OVERWRITE_TEXT,true, context);
        overwriteButton.changeFontColor(PURPLE_TEXT_COLOR);
    }

    private void createDeleteButton(int x) {
        String TEXT = "Delete";
        int PAD = 50;
        int DELETE_BTN_X = x + BTN_WIDTH + PAD;
        int DELETE_BTN_Y = Y + MENU_HEADING_HEIGHT + MENU_HEIGHT + PAD + MENU_HEADING_HEIGHT + PAD;

        deleteSaveButton = new MenuItem(DELETE_BTN_X, DELETE_BTN_Y, MENU_HEADING_HEIGHT, BTN_WIDTH, TEXT,true, context);
        deleteSaveButton.changeFontColor(RED_TEXT_COLOR);
    }

    private void createInfoButton(int x) {
        String TEXT = "Save Info";
        int BUTTON_Y_PAD = 50;
        int INFO_BTN_Y = Y + MENU_HEADING_HEIGHT + MENU_HEIGHT + BUTTON_Y_PAD + MENU_HEADING_HEIGHT + BUTTON_Y_PAD + MENU_HEADING_HEIGHT + BUTTON_Y_PAD;

        saveInfoButton = new MenuItem(x, INFO_BTN_Y, MENU_HEADING_HEIGHT, BTN_WIDTH, TEXT,true, context);
    }

    public void checkForUserTouch(float eventX, float eventY, GamePresenter presenter) {
        if (isClosed) {
            return;
        }

        if (confirmPopUp != null) {
            handleConfirmPopUp(eventX, eventY, presenter);
            return;
        }

        if (alertPopUp != null) {
            handleAlertPopUp(eventX, eventY, presenter);
            return;
        }

        handleRadioBtnInteraction(eventX, eventY, presenter);

        handleNewSaveButton((int) eventX, (int) eventY, presenter);
        handleOverwriteButton((int) eventX, (int) eventY, presenter);
        handleDeleteButton((int) eventX, (int) eventY, presenter);
        handleLoadButton((int) eventX, (int) eventY, presenter);
        handleInfoButton((int) eventX, (int) eventY, presenter);
    }

    private void handleConfirmPopUp(float eventX, float eventY, GamePresenter presenter) {
        boolean userTouchedPopUp = confirmPopUp.didUserTouchButton(eventX, eventY, presenter);
        if (userTouchedPopUp) {
            boolean didUserConfirm = confirmPopUp.didUserConfirm();

            boolean isNewSavePopUp = Objects.equals(confirmPopUp.getMessage(), newSaveMsg);
            if (didUserConfirm && isNewSavePopUp) {
                presenter.addSaveSlot();
                String alertMsg = context.getString(R.string.newSaveAlert);
                alertPopUp = new AlertPopUp(alertMsg, context, true);
            }

            boolean isOverwritePopUp = Objects.equals(confirmPopUp.getMessage(), overwriteMsg);
            if (didUserConfirm && isOverwritePopUp) {
                int index = presenter.getIndexOfSaveSlot(selectedSaveSlot) - 1;
                presenter.overwriteSaveSlot(index);
                String alertMsg = context.getString(R.string.overwriteAlert);
                alertPopUp = new AlertPopUp(alertMsg, context, true);

                selectedSaveSlot = null;
            }

            boolean isDeletePopUp = Objects.equals(confirmPopUp.getMessage(), deleteMsg);
            if (didUserConfirm && isDeletePopUp) {
                int index = presenter.getIndexOfSaveSlot(selectedSaveSlot) - 1;
                presenter.deleteSaveSlot(index);
                String alertMsg = context.getString(R.string.deleteAlert);
                alertPopUp = new AlertPopUp(alertMsg, context, true);

                selectedSaveSlot = null;
            }

            boolean isLoadPopUp = Objects.equals(confirmPopUp.getMessage(), loadMsg);
            if (didUserConfirm && isLoadPopUp) {
                int saveSlotNum = presenter.getIndexOfSaveSlot(selectedSaveSlot);
                int index = saveSlotNum - 1;
                presenter.loadSaveSlot(index);

                loadSaveMsg = context.getString(R.string.loadAlert, saveSlotNum);
                alertPopUp = new AlertPopUp(loadSaveMsg, context, true);

                selectedSaveSlot = null;
            }

            updateSaveSlotVisuals();
            confirmPopUp = null;
        }
    }

    private void handleAlertPopUp(float eventX, float eventY, GamePresenter presenter) {
        boolean userClosePopUp = alertPopUp.didUserClosePopUp(eventX, eventY, presenter);

        if (!userClosePopUp) {
            return;
        }

        boolean didUserLoadSave = Objects.equals(alertPopUp.getMessage(), loadSaveMsg);
        if (didUserLoadSave) {
            alertPopUp = null;
            gameView.closeSaveMenu();
        }

        alertPopUp = null;
    }

    private void handleRadioBtnInteraction(float eventX, float eventY, GamePresenter presenter) {
        String radioBtnPressed = saveRadioBtnList.checkForBtnPressAndCheckBtn(eventX, eventY, presenter);
        if (!Objects.equals(radioBtnPressed, "")) {
            selectedSaveSlot = radioBtnPressed;
        }
    }

    private void handleNewSaveButton(int eventX, int eventY, GamePresenter presenter) {
        boolean hasNewSaveBeenPressed = hasBtnBeenPressed(newSaveSlotButton, eventX, eventY, presenter);
        if (!hasNewSaveBeenPressed) {
            return;
        }

        boolean reachedMaxSaves = presenter.hasMaxSaves();
        if (reachedMaxSaves) {
            String alertMsg = context.getString(R.string.maxSavesReached);
            alertPopUp = new AlertPopUp(alertMsg, context, true);
            return;
        }

        newSaveMsg = context.getString(R.string.newSaveConfirmation);
        confirmPopUp = new ConfirmPopUp(newSaveMsg, context, true);
    }

    private void handleOverwriteButton(int eventX, int eventY, GamePresenter presenter) {
        boolean hasNewSaveBeenPressed = hasBtnBeenPressed(overwriteButton, eventX, eventY, presenter);
        if (!hasNewSaveBeenPressed) {
            return;
        }

        if (selectedSaveSlot == null) {
            String alertMsg = context.getString(R.string.selectSaveSlot);
            alertPopUp = new AlertPopUp(alertMsg, context, true);
            return;
        }

        int saveSlotNumber = presenter.getIndexOfSaveSlot(selectedSaveSlot);
        overwriteMsg = context.getString(R.string.overwriteSaveConfirmation, saveSlotNumber);
        confirmPopUp = new ConfirmPopUp(overwriteMsg, context, true);
    }

    private void handleDeleteButton(int eventX, int eventY, GamePresenter presenter) {
        boolean hasNewSaveBeenPressed = hasBtnBeenPressed(deleteSaveButton, eventX, eventY, presenter);
        if (!hasNewSaveBeenPressed) {
            return;
        }

        if (selectedSaveSlot == null) {
            String alertMsg = context.getString(R.string.selectSaveSlot);
            alertPopUp = new AlertPopUp(alertMsg, context, true);
            return;
        }

        int saveSlotNumber = presenter.getIndexOfSaveSlot(selectedSaveSlot);
        deleteMsg = context.getString(R.string.deleteConfirmation, saveSlotNumber);
        confirmPopUp = new ConfirmPopUp(deleteMsg, context, true);
    }

    private void handleLoadButton(int eventX, int eventY, GamePresenter presenter) {
        boolean hasNewSaveBeenPressed = hasBtnBeenPressed(loadSaveButton, eventX, eventY, presenter);
        if (!hasNewSaveBeenPressed) {
            return;
        }

        if (selectedSaveSlot == null) {
            String alertMsg = context.getString(R.string.selectSaveSlot);
            alertPopUp = new AlertPopUp(alertMsg, context, true);
            return;
        }

        int saveSlotNumber = presenter.getIndexOfSaveSlot(selectedSaveSlot);
        loadMsg = context.getString(R.string.loadConfirmation, saveSlotNumber);
        confirmPopUp = new ConfirmPopUp(loadMsg, context, true);
    }

    private void handleInfoButton(int eventX, int eventY, GamePresenter presenter) {
        boolean hasNewSaveBeenPressed = hasBtnBeenPressed(saveInfoButton, eventX, eventY, presenter);
        if (!hasNewSaveBeenPressed) {
            return;
        }

        if (selectedSaveSlot == null) {
            String alertMsg = context.getString(R.string.selectSaveSlot);
            alertPopUp = new AlertPopUp(alertMsg, context, true);
            return;
        }

        int index = presenter.getIndexOfSaveSlot(selectedSaveSlot) - 1;
        String alertMsg = presenter.getSaveFileInfo(index);
        alertPopUp = new AlertPopUp(alertMsg, context, false);
    }

    private void updateSaveSlotVisuals() {
        ArrayList<String> saveSlotList = presenter.getSaveSlotList();
        saveRadioBtnList.updateRadioBtnList(saveSlotList);
    }

    public void draw(Canvas canvas, Paint paint){
        if (isClosed) {
            return;
        }

        drawOverlay(canvas, paint);
        drawMenuItems(canvas, paint);
        saveRadioBtnList.draw(canvas, paint);
        overwriteButton.draw(canvas, paint);
        newSaveSlotButton.draw(canvas, paint);
        loadSaveButton.draw(canvas, paint);
        deleteSaveButton.draw(canvas, paint);
        saveInfoButton.draw(canvas, paint);
        drawPopUps(canvas, paint);
    }
}
