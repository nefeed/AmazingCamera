package com.gavin.amazingcamera.entity;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public class IDCardFrontInfo {
    //身份证号数据
    private String idNum;		//身份证号识别结果
    private char[] idName;      //身份信息
    private char[] idAddress;   //地址信息
    private char[] idNation;    //民族信息
    private char idSex;         //性别信息

    public IDCardFrontInfo(int preWidth, int preHeight)
    {
        idNum = "";
        idName = new char[16];
        idAddress = new char[128];
        idNation = new char[8];
        idSex = '男';
    }

    public void CleanInfo()
    {
        idNum = "";
        idName = new char[16];
        idAddress = new char[128];
        idNation = new char[8];

        idSex = '男';
    }

    public String getIdNumStr() {
        return idNum;
    }

    public String getIdNameStr() {
        return String.valueOf(idName);
    }

    public String getIdNationStr() {
        return String.valueOf(idNation);
    }

    public String getIdSexStr() {
        return String.valueOf(idSex);
    }

    public String getIdAddressStr() {
        return String.valueOf(idAddress);
    }

    public String getIdBirthStr() {
        return idNum.substring(6, 14);
    }
};
