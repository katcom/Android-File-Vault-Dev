package com.androidprivacy.demo1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChooseFileDialog extends DialogFragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private FileManager mFileManager;
    private ChooseFileListener mChooseFileListener;

    public interface ChooseFileListener {
        void getChosenFile(String filename,String filepath);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Load view
        View view = inflater.inflate(R.layout.dialog_choose_file, container);
        mFileManager = FileManager.getInstance(getContext());
        mRecyclerView = view.findViewById(R.id.choose_file_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateUI();
        return view;
    }
    private void updateUI() {
        mFileManager.update();
        mAdapter = new ItemAdapter(mFileManager.getEncryptedFiles());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private MyFile mFile;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }
        public void bindFile(MyFile file){
            mFile = file;
            mTextView.setText(mFile.getFilename());
        }


        @Override
        public void onClick(View view) {
            mChooseFileListener = (ChooseFileListener) getActivity();
            mChooseFileListener.getChosenFile(mFile.getFilename(),mFile.getFilepath());

            //Toast.makeText(getActivity(),("Get file: " + mFile.getFilename()),Toast.LENGTH_LONG);
            Log.v("ChooseFileDialog", mFile.toString());

            dismiss();
        }
    }

    private class ItemAdapter extends  RecyclerView.Adapter<ItemHolder>{
        private List<MyFile> mFiles;
        public ItemAdapter(List<MyFile> files){
            mFiles = files;
        }
        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater
                    .inflate(R.layout.item_text, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int i) {
            //holder.mTextView.setText(mFiles.get(i).getFilename());
            holder.bindFile(mFiles.get(i));
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }


    }

}
