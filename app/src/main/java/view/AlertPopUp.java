package view;

import static view.ViewConstants.CANCEL_TEXT_COLOR;
import static view.ViewConstants.CONFIRM_TEXT_COLOR;

import android.content.Context;

import com.example.anomalousencounters.R;

import java.util.Objects;

import presenter.GamePresenter;

public class AlertPopUp extends PopUpBox{
    private MenuItem okayBtn;
    private String okayText;


    protected AlertPopUp(String message, Context context) {
        super(message, context);

        int okayX = messageBox.getX();
        int buttonsY = messageBox.getY() + messageBox.getHeight();
        int WIDTH = messageBox.getWidth();
        int HEIGHT = 80;

        okayText = context.getString(R.string.okMessage);
        okayBtn = new MenuItem(okayX, buttonsY, HEIGHT, WIDTH, okayText, true, context);
        buttons.add(okayBtn);

    }

    public boolean didUserClosePopUp(float eventX, float eventY, GamePresenter presenter) {
        return checkForUserTouchOnButton(okayBtn, eventX, eventY, presenter);
    }
}
