package org.synyx.urlaubsverwaltung.mail;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.synyx.urlaubsverwaltung.person.Person;
import org.synyx.urlaubsverwaltung.person.PersonService;
import org.synyx.urlaubsverwaltung.user.UserSettingsService;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation of interface {@link MailService}.
 */
@Service("mailService")
@EnableConfigurationProperties(MailProperties.class)
class MailServiceImpl implements MailService {

    private static final Logger LOG = getLogger(lookup().lookupClass());

    private final MessageSource emailMessageSource;
    private final ITemplateEngine emailTemplateEngine;
    private final MailSenderService mailSenderService;
    private final MailProperties mailProperties;
    private final PersonService personService;
    private final UserSettingsService userSettingsService;

    @Autowired
    MailServiceImpl(MessageSource emailMessageSource, ITemplateEngine emailTemplateEngine, MailSenderService mailSenderService,
                    MailProperties mailProperties, PersonService personService, UserSettingsService userSettingsService) {
        this.emailMessageSource = emailMessageSource;
        this.emailTemplateEngine = emailTemplateEngine;
        this.mailProperties = mailProperties;
        this.mailSenderService = mailSenderService;
        this.personService = personService;
        this.userSettingsService = userSettingsService;
    }

    @Async
    @Override
    public void send(Mail mail) {

        final List<Person> recipients = getRecipients(mail);
        final Map<Person, Locale> effectiveLocales = userSettingsService.getEffectiveLocale(recipients);

        recipients.forEach(recipient -> {

            final Locale effectiveLocale = effectiveLocales.get(recipient);

            final Context context = new Context(effectiveLocale);
            context.setVariables(mail.getTemplateModel());
            context.setVariable("baseLinkURL", getApplicationUrl());
            context.setVariable("rightPadder", RightPadder.getInstance());
            context.setVariable("recipient", recipient);

            final String sender = generateMailAddressAndDisplayName(mailProperties.getSender(), mailProperties.getSenderDisplayName());
            final String email = recipient.getEmail();
            final String subject = getTranslation(effectiveLocale, mail.getSubjectMessageKey(), mail.getSubjectMessageArguments());
            final String body = emailTemplateEngine.process(mail.getTemplateName(), context);

            if (email != null) {
                mail.getMailAttachments().ifPresentOrElse(
                    mailAttachments -> mailSenderService.sendEmail(sender, email, subject, body, mailAttachments),
                    () -> mailSenderService.sendEmail(sender, email, subject, body)
                );
            } else {
                LOG.debug("Could not send mail to E-Mail-Address of person with id {}, because email is null.", recipient.getId());
            }
        });
    }

    private List<Person> getRecipients(Mail mail) {

        final List<Person> recipients = new ArrayList<>();
        mail.getMailNotificationRecipients().ifPresent(mailNotification -> recipients.addAll(personService.getActivePersonsWithNotificationType(mailNotification)));
        mail.getMailAddressRecipients().ifPresent(recipients::addAll);

        if (mail.isSendToTechnicalMail()) {
            recipients.add(new Person(null, null, "Administrator", mailProperties.getAdministrator()));
        }

        return recipients.stream().distinct().collect(Collectors.toList());
    }

    private String getTranslation(Locale locale, String key, Object... args) {
        return emailMessageSource.getMessage(key, args, locale);
    }

    private String getApplicationUrl() {
        final String applicationUrl = mailProperties.getApplicationUrl();
        return applicationUrl.endsWith("/") ? applicationUrl : applicationUrl + "/";
    }

    private String generateMailAddressAndDisplayName(String address, String displayName) {
        return String.format("%s <%s>", displayName, address);
    }
}
