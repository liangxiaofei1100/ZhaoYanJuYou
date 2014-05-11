
package com.zhaoyan.juyou.backuprestore;

import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.zhaoyan.juyou.backuprestore.MmsXmlInfo.MmsXml;

public class MmsXmlComposer {
    private XmlSerializer mSerializer = null;
    private StringWriter mStringWriter = null;

    public boolean startCompose() {
        boolean result = false;
        mSerializer = Xml.newSerializer();
        mStringWriter = new StringWriter();
        try {
            mSerializer.setOutput(mStringWriter);
            // serializer.startDocument("UTF-8", null);
            mSerializer.startDocument(null, false);
            mSerializer.startTag("", MmsXml.ROOT);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean endCompose() {
        boolean result = false;
        try {
            mSerializer.endTag("", MmsXml.ROOT);
            mSerializer.endDocument();
            result = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean addOneMmsRecord(MmsXmlInfo record) {
        boolean result = false;
        try {
            mSerializer.startTag("", MmsXml.RECORD);
            //mSerializer.attribute("", MmsXml.CATEGORY, record.getCategory());
            mSerializer.attribute("", MmsXml.ID, record.getID());
            mSerializer.attribute("", MmsXml.ISREAD, record.getIsRead());
            //mSerializer.attribute("", MmsXml.LOCALDATE, record.getLocalDate());
            //mSerializer.attribute("", MmsXml.ST, record.getST());
            mSerializer.attribute("", MmsXml.MSGBOX, record.getMsgBox());
            mSerializer.attribute("", MmsXml.DATE, record.getDate());
            mSerializer.attribute("", MmsXml.SIZE, record.getSize());
            mSerializer.attribute("", MmsXml.SIMID, record.getSimId());
            mSerializer.attribute("", MmsXml.ISLOCKED, record.getIsLocked());
            mSerializer.endTag("", MmsXml.RECORD);

            result = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getXmlInfo() {
        if (mStringWriter != null) {
            return mStringWriter.toString();
        }

        return null;
    }
}
