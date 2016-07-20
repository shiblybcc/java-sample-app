package com.example.controller;


import com.example.domain.FileEncryption;
import com.example.domain.ScannedFile;
import com.example.repository.ScannedFileRepository;
import com.example.view.ScannedFileView;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class FileUploadBean {

    @ManagedProperty(value = "#{scannedFile}")
    private ScannedFileView scannedFile;

    @ManagedProperty(value = "#{scannedFileRepository}")
    ScannedFileRepository scannedFileRepository;

    private Part file;

    public String upload() throws IOException, GeneralSecurityException, DecoderException {
        InputStream inputStream = file.getInputStream();

        FileEncryption fileEncryption = new FileEncryption();
        byte[] bytes = fileEncryption.encryptImage(inputStream);

        ScannedFile created = new ScannedFile();
        created.setId(this.scannedFile.getId());
        created.setFallnummer(this.scannedFile.getFallnummer());
        created.setKey(fileEncryption.encryptKey());
        created.setScannedImage(bytes);
        ScannedFile newScannedFile = this.scannedFileRepository.save(created);
        inputStream.close();
        return "index.xhtml";
    }

    public void download() throws IOException, GeneralSecurityException, DecoderException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
//        ec.setResponseContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
//        ec.setResponseContentLength(contentLength); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + "testing" + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

        OutputStream output = ec.getResponseOutputStream();
        // Now you can write the InputStream of the file to the above OutputStream the usual way.
        InputStream inputStream = file.getInputStream();

        String scannedFileId = fc.getExternalContext().getRequestParameterMap().get("uploadKey:scannedFileId");
        ScannedFile scannedFile = scannedFileRepository.findOne(Long.valueOf(scannedFileId));
        InputStream is = new ByteArrayInputStream(scannedFile.getScannedImage());
        FileEncryption fileEncryption = new FileEncryption();
        fileEncryption.decryptKey(scannedFile.getKey(), inputStream);
        fileEncryption.decryptImage(is);

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
    }


    public void findFileById() throws GeneralSecurityException, DecoderException, IOException {
        ScannedFile found = scannedFileRepository.findOne(this.scannedFile.getId());
        this.scannedFile.setId(found.getId());
        this.scannedFile.setFallnummer(found.getFallnummer());
        this.scannedFile.setScannedImage(found.getScannedImage());
    }

    public List<ScannedFileView> findAllScannedFiles() {
        List<ScannedFileView> scannedFiles = new ArrayList<>();
        for (ScannedFile entity : this.scannedFileRepository.findAll()) {
            ScannedFileView view = new ScannedFileView();
            view.setId(entity.getId());
            view.setFallnummer(entity.getFallnummer());
            scannedFiles.add(view);
        }
        return scannedFiles;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public ScannedFileView getScannedFile() {
        return scannedFile;
    }

    public void setScannedFile(ScannedFileView scannedFile) {
        this.scannedFile = scannedFile;
    }

    public ScannedFileRepository getScannedFileRepository() {
        return scannedFileRepository;
    }

    public void setScannedFileRepository(ScannedFileRepository scannedFileRepository) {
        this.scannedFileRepository = scannedFileRepository;
    }
}
