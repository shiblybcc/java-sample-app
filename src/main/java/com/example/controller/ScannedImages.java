package com.example.controller;

import com.example.domain.FileEncryption;
import com.example.domain.ScannedFile;
import com.example.repository.ScannedFileRepository;
import org.apache.commons.codec.DecoderException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@ManagedBean
@ApplicationScoped
public class ScannedImages {

    @ManagedProperty(value = "#{scannedFileRepository}")
    ScannedFileRepository scannedFileRepository;


    public StreamedContent getImage() throws IOException, DecoderException, GeneralSecurityException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        }
        else {
            String scannedFileId = context.getExternalContext().getRequestParameterMap().get("scannedFileId");
            ScannedFile scannedFile = scannedFileRepository.findOne(Long.valueOf(scannedFileId));

            InputStream is = new ByteArrayInputStream(scannedFile.getScannedImage());
//            InputStream is = new ByteArrayInputStream(scannedFile.getScannedImage());
            FileEncryption fileEncryption = new FileEncryption();
//            fileEncryption.decryptKey(scannedFile.getKey());


            return new DefaultStreamedContent(fileEncryption.decryptImage(is));
        }
    }

    public ScannedFileRepository getScannedFileRepository() {
        return scannedFileRepository;
    }

    public void setScannedFileRepository(ScannedFileRepository scannedFileRepository) {
        this.scannedFileRepository = scannedFileRepository;
    }

}
