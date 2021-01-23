package com.androidprivacy.demo1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity implements  ChooseFileDialog.ChooseFileListener {
    private static final String TAG ="demo1";
    private RecyclerView mRecyclerView;
    private RecyclerView mDecryptedFileRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Button mEncryptButton;
    private Button mDecryptButton;
    private ImageView mImageView;
    private  List<MyFile> myFiles;
    private  FileManager mFileManager;
    String keyAlias = "MyKey";

    private final static int REQUEST_FILE_CODE = 1;
    private final static int REQUEST_CHOOSE_FILE_DIALOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileManager = FileManager.getInstance(this);

        mEncryptButton = findViewById(R.id.button_encrypt);
        mEncryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFile();
            }
        });
        mDecryptButton = findViewById(R.id.button_decrypt);

        mDecryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseFileDialog();

            }
        });
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDecryptedFileRecyclerView = findViewById(R.id.decrypted_files_recycler_view);
        mDecryptedFileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    private void updateUI() {
        mFileManager.update();
        mAdapter = new ItemAdapter(mFileManager.getEncryptedFiles());
        mRecyclerView.setAdapter(mAdapter);

        mDecryptedFileRecyclerView.setAdapter(new ItemAdapter(mFileManager.getDecryptedFiles()));
    }

    /**
     * When user has chosen a file, decrypt and save it to the private storage.
     * ChooseFileDialog would call this method when the user has chosen a file.
     * @param filepath
     */
    @Override
    public void getChosenFile(String filename,String filepath) {
        Toast.makeText(this,"Get file: " + filepath,Toast.LENGTH_LONG).show();

        mFileManager.decryptAndSaveInPrivateStorage(keyAlias,filename,filepath);
        updateUI();

    }

    /**
     * Open the ChooseFileDialog for user to choose a file
     * When the user have chosen a file, the dialog calls back to the activity.
     */
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent,REQUEST_FILE_CODE);

    }



    /**
     * Show the dialog to choose a file for decryption
     */
    void showChooseFileDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // Create and show the dialog.
        DialogFragment newFragment = new ChooseFileDialog();
        newFragment.show(ft,"dialog");
    }





    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) return;

        if(requestCode == REQUEST_FILE_CODE && data != null){
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    Uri SelectedFile = data.getClipData().getItemAt(i).getUri();
                    importAndEncryptFile(SelectedFile);
                }
            }
        }
    }

    /**
     * Import to the private storage the file selected by the user.
     *
     * @param uri
     */
    private void importAndEncryptFile(Uri uri) {
        // TODO (Areesh and Ruch)

        // Log.v("DEMO_test",file.getAbsolutePath());

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            String filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            Log.v(TAG,filename);
            try {
                mFileManager.encryptAndSaveInPrivateStorage(keyAlias,contentResolver.openInputStream(uri),filename);
                Toast.makeText(this,"Select file: "+filename,Toast.LENGTH_LONG).show();
                updateUI();
            } catch (FileNotFoundException e) {
                Log.e(TAG,e.toString());
            }
        }
    }

    /**
     * This method allow user to choose the file to be decrypted and a location to which the decrypted file is then sent.
     */
    private void exportAndDecryptFile(){
        // TODO
    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textView);
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
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater
                    .inflate(R.layout.item_text, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int i) {
            holder.mTextView.setText(mFiles.get(i).getFilename());
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }

}
