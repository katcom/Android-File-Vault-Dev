package com.androidprivacy.demo1;

class MyFile{

    private String filename;
    private String filepath;
    public MyFile(String filename,String filepath){
        this.filename = filename;
        this.filepath = filepath;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public String toString(){
        String text = "File name : " + filename + "\n";
        text += "File path: " + filepath +"\n";
        return text;
    }

}
