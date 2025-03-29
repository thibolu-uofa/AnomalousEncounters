package view;

import static view.ViewConstants.CANCEL_TEXT_COLOR;
import static view.ViewConstants.CONFIRM_TEXT_COLOR;

import android.content.Context;

import com.example.anomalousencounters.R;

import java.util.Objects;

import presenter.GamePresenter;

public class ConfirmPopUp extends  PopUpBox{
    String cancelText;
    String confirmText;
    boolean hasConfirmed;

    public ConfirmPopUp(String message, Context context) {
        super(message, context);

        int cancelX = messageBox.getX();
        int buttonsY = messageBox.getY() + messageBox.getHeight();
        int WIDTH = messageBox.getWidth()/2;
        int HEIGHT = 80;

        cancelText = context.getString(R.string.cancelAction);
        MenuItem cancelBtn = new MenuItem(cancelX, buttonsY, HEIGHT, WIDTH, cancelText, true, context);
        cancelBtn.changeFontColor(CANCEL_TEXT_COLOR);
        buttons.add(cancelBtn);

        int confirmX = cancelX + WIDTH;
        confirmText = context.getString(R.string.confirmAction);
        MenuItem confirmBtn = new MenuItem(confirmX, buttonsY, HEIGHT, WIDTH, confirmText, true, context);
        confirmBtn.changeFontColor(CONFIRM_TEXT_COLOR);
        buttons.add(confirmBtn);
    }

    public boolean didUserTouchButton(float eventX, float eventY, GamePresenter presenter) {
        for (MenuItem button: buttons){
            boolean hasUserTouchedButton = checkForUserTouchOnButton(button, eventX, eventY, presenter);
            if (hasUserTouchedButton) {
                if (Objects.equals(button.getText(), confirmText)) {
                    hasConfirmed = true;
                } else {
                    hasConfirmed = false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean didUserConfirm(){
        return hasConfirmed;
    }
}
