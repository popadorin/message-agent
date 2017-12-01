package com.dorin.helpers;

import com.dorin.messagebroker.MessageQueue;
import org.apache.log4j.Logger;

import java.io.*;

public class MQBackuper {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private final String MQ_PATH;

    public MQBackuper(String path) {
        this.MQ_PATH = path;
    }

    public void backupMessageQueue(MessageQueue messageQueue) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File(MQ_PATH)));

            oos.writeObject(messageQueue);
            oos.close();
        } catch (IOException e) {
            LOGGER.error("Output exception to write in messagequeue-backup file");
        }

    }

    public MessageQueue readBackup() {
        MessageQueue messageQueue = null;

        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(new File(MQ_PATH)));
            messageQueue = (MessageQueue) ois.readObject();

        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException occurred");
        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException occurred");
        } catch (IOException e) {
            LOGGER.error("IOException occurred");
        }

        return messageQueue;
    }

}
