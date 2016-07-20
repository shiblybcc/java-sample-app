package com.example.domain;

import javax.persistence.*;
import java.sql.Blob;

@Entity
public class ScannedFile {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = true)
    private Integer fallnummer;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(nullable = true)
    private byte[] key;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    protected  byte[]  scannedImage;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdToolID() {
        return fallnummer;
    }

    public Integer getFallnummer() {
        return fallnummer;
    }

    public void setFallnummer(Integer fallnummer) {
        this.fallnummer = fallnummer;
    }

    public byte[] getScannedImage() {
        return scannedImage;
    }

    public void setScannedImage(byte[] scannedImage) {
        this.scannedImage = scannedImage;
    }
}
