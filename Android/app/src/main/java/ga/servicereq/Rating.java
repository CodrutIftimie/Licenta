package ga.servicereq;

import android.app.AlertDialog;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class Rating {

    public static void show(LayoutInflater inflater, final View view) {
        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(view.getContext());
        final View deleteDialogView = inflater.inflate(R.layout.layout_rating, null);
        final RatingBar rating = deleteDialogView.findViewById(R.id.rating_rateBar);
        final Button okButton = deleteDialogView.findViewById(R.id.rating_btnOk);
        final Button cancelButton = deleteDialogView.findViewById(R.id.rating_btnCancel);
        final TextView ratingFor = deleteDialogView.findViewById(R.id.rating_ratetv);
        final TextView userName = view.findViewById(R.id.messagepv_receiverName);

        String receiverName = "Evaluare pentru " + userName.getText().toString();
        ratingFor.setText(receiverName);

        deleteDialogBuilder.setView(deleteDialogView);
        final AlertDialog deleteDialog = deleteDialogBuilder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senderId = (String)view.getTag();
                Server.sendMessage("E;"+ senderId + ";" + rating.getRating() + ";;");

                Toast.makeText(view.getContext(),"Mulțumim pentru evaluarea utilizatorului!", Toast.LENGTH_SHORT).show();
                deleteDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }
}
