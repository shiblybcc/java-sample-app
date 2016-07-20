package com.example.view;


import com.example.domain.ScannedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "scannedFile")
@ViewScoped
public class ScannedFileView extends ScannedFile {

    public ScannedFileView(){}
}
