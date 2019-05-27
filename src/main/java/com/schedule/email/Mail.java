package com.schedule.email;

import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.LoadProperties;
import com.dvd.ckp.bean.ConfigEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 *
 * @author viettx
 */
public class Mail {

    private static final Logger logger = Logger.getLogger(Mail.class);
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean sendMail(String from, String to, String content, String title, String pass) {
        boolean isSend = false;
        try {
            LoadProperties properties = new LoadProperties();
            ConfigEntity entity = properties.loadConfig();
            // Set mail properties
            Properties props = System.getProperties();
            props.put(Constants.SMTP_STARTTLS, Constants.TRUE);
            props.put(Constants.SMTP_HOST, entity.getHost());
            props.put(Constants.SMTP_USER, from);
            props.put(Constants.SMTP_PASSWORD, pass);
            props.put(Constants.SMTP_PORT, entity.getPort());
            props.put(Constants.SMTP_AUTH, Constants.TRUE);

            Session session = Session.getDefaultInstance(props);
            MimeMessage message = new MimeMessage(session);

            try {
                // Set email data
                message.setFrom(new InternetAddress(from));

                message.setSubject(title, Constants.UTF8);
                MimeMultipart multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();

                // Set key values
                // Map<String, String> input = new HashMap<String, String>();
                // input.put("Author", "java2db.com");
                // input.put("Topic", "HTML Template for Email");
                // input.put("Content In", "English");
                InternetAddress[] recipient = getInternetAddresses(to);
                message.addRecipients(Message.RecipientType.TO, recipient);

                // Add CC
                // InternetAddress[] CcAddress =
                // getInternetAddresses(entity.getCc());
                // message.setRecipients(javax.mail.Message.RecipientType.CC,
                // CcAddress);
                //
                // // Add BBC
                // InternetAddress[] BccAddress =
                // getInternetAddresses(entity.getBcc());
                // message.setRecipients(javax.mail.Message.RecipientType.BCC,
                // BccAddress);
                // HTML mail content
                // String htmlText =
                // readEmailFromHtml("C:/mail/HTMLTemplate.html", input);
                messageBodyPart.setContent(content, Constants.HTML_UTF_8);

                multipart.addBodyPart(messageBodyPart);

//                 Add attachments
//                List<String> attachment
//                        = getListAttachment(path);
//
//                if (!attachment.isEmpty()) {
//                    for (String fileAttachment : attachment) {
//                        messageBodyPart = new MimeBodyPart();
//                        DataSource source = new FileDataSource(fileAttachment);
//                        messageBodyPart.setDataHandler(new DataHandler(source));
//                        messageBodyPart.setFileName(source.getName());
//                        multipart.addBodyPart(messageBodyPart);
//                    }
//
//                }
//                message.setContent(multipart);

                // Conect to smtp server and send Email
                Transport transport = session.getTransport(Constants.SMTP);
                transport.connect(entity.getHost(), entity.getMailSend(), entity.getPassword());
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
                isSend = true;
            } catch (MessagingException ex) {
                logger.error(ex.getMessage(), ex);

            } catch (Exception ae) {
                logger.error(ae.getMessage(), ae);

            }
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
        return isSend;
    }

    // Method to replace the values for keys
    protected String readEmailFromHtml(String filePath, Map<String, String> input) {
        String msg = readContentFromFile(filePath);
        try {
            Set<Entry<String, String>> entries = input.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                msg = msg.replace(entry.getKey().trim(), entry.getValue().trim());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return msg;
    }

    // Method to read HTML file as a String
    private String readContentFromFile(String fileName) {
        StringBuffer contents = new StringBuffer();

        try {
            // use buffering, reading one line at a time
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            try {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return contents.toString();
    }

    private static InternetAddress[] getInternetAddresses(String recipients) throws AddressException {
        ArrayList<String> recipientsArray = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(recipients, ";");

        while (st.hasMoreTokens()) {
            recipientsArray.add(st.nextToken());
        }

        int sizeTo = recipientsArray.size();
        InternetAddress[] ainternetaddress1 = new InternetAddress[sizeTo];
        for (int i = 0; i < sizeTo; i++) {
            ainternetaddress1[i] = new InternetAddress(recipientsArray.get(i).toString());
        }
        return ainternetaddress1;
    }

    private List<String> getListAttachment(String value) {
        String[] arrayValue = value.split(";");
        List<String> lstAttachment = Arrays.asList(arrayValue);
        return lstAttachment;
    }

}
