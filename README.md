## Mailer application was created to have a library to send mail.

#### The application has default servlet `MailServlet` and library part, that contains files 'Constants', 'MailAuthentificator', 'MailMessage'

The example of usage is in the MailerServlet, this is simple to use library:
```java
MailMessage mailMessage = new MailMessage(
  getContextParam(PROP_MAIL_FROM),
  recipients,
  subject,
  message
);
mailMessage.setFilesToSend(getParts(request));
String user = getContextParam(PROP_MAIL_USER);

Session session;
if (user == null || user.isEmpty()) {
  session = Session.getInstance(properties);
} else {
  session = Session.getInstance(
    properties,
    new MailAuthenticator(user, getContextParam(PROP_MAIL_PASSWORD))
  );
}
mailMessage.send(mailMessage.buildMessage(session));
```

1. Step one is to create MailMessage object with all parameters. It has variability to construct object and methods to set files and add recipients.
```java
MailMessage mailMessage = new MailMessage(
  getContextParam(PROP_MAIL_FROM),
  recipients,
  subject,
  message
);
 mailMessage.setFilesToSend(getParts(request));
```

2. Second part is - create Session from javax.mail package. In servlet, used this construction to create session if there is user parameter in `web.xml`, or not
```java
String user = getContextParam(PROP_MAIL_USER);
Session session;

if (user == null || user.isEmpty()) {
  session = Session.getInstance(properties);
} else {
  session = Session.getInstance(
    properties,
    new MailAuthenticator(user, getContextParam(PROP_MAIL_PASSWORD))
  );
}
```

3. Last part is building and sending mail
```java
MimeMessage mail = mailMessage.buildMessage(session);
mailMessage.send(mail);
```
