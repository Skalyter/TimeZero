package com.example.timezero.routines;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.timezero.R;
import com.example.timezero.database.RoutineDAO;
import com.example.timezero.model.Routine;

public class FragmentAddEditRoutine extends DialogFragment {

    public interface OnDialogResult{
        void onDialogOK();
        void onDialogCanceled();
    }
    EditText title;
    RoutineDAO routineDAO;
    Routine routine;
    long routineId;
    OnDialogResult onDialogResult;

    public FragmentAddEditRoutine() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_routine, null);
        routineId = FragmentRoutines.getRoutineId();
        if(routineId>=0) {
            routineDAO = new RoutineDAO(getContext());
            routine = routineDAO.getRoutineByID(routineId);
            title = view.findViewById(R.id.routine_dialog_title);
            title.setText(routine.getTitle());
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        title = getDialog().findViewById(R.id.routine_dialog_title);
                        String titleInput = title.getText().toString();
                        if(titleInput.equals("")){
                            title.setError("Enter routine's title");
                        } else {
                            if(routineId==-1){
                                routineDAO = new RoutineDAO(getContext());
                                routine = new Routine();
                                routine.setTitle(titleInput);
                                routine.setActive(true);
                                routineDAO.insertRoutine(routine);
                                Toast.makeText(getActivity(), "Routine added successfully", Toast.LENGTH_SHORT).show();
                            } else{
                                routine.setTitle(titleInput);
                                routineDAO.updateRoutine(routine);
                                Toast.makeText(getActivity(), "Routine updated successfully", Toast.LENGTH_SHORT).show();
                            }
                            onDialogResult.onDialogOK();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FragmentAddEditRoutine.this.getDialog().cancel();
                        onDialogResult.onDialogCanceled();
                    }
                });

        return builder.create();
    }

    public OnDialogResult getOnDialogResult() {
        return onDialogResult;
    }

    public void setOnDialogResult(OnDialogResult onDialogResult) {
        this.onDialogResult = onDialogResult;
    }
}
