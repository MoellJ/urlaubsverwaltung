package org.synyx.urlaubsverwaltung.extension.sicknote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.synyx.urlaubsverwaltung.sicknote.sicknote.SickNoteCreatedEvent;
import org.synyx.urlaubsverwaltung.sicknote.sicknote.SickNoteService;

import java.time.Clock;
import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Component
@ConditionalOnProperty(value = "uv.extensions.sicknote.republish.enabled", havingValue = "true")
@ConditionalOnBean(SickNoteEventHandlerExtension.class)
public class SickNoteEventRepublisher {

    private static final Logger LOG = LoggerFactory.getLogger(SickNoteEventRepublisher.class);

    private final SickNoteService sickNoteService;
    private final SickNoteEventHandlerExtension sickNoteEventHandlerExtension;
    private final Clock clock;

    SickNoteEventRepublisher(SickNoteService sickNoteService, SickNoteEventHandlerExtension sickNoteEventHandlerExtension, Clock clock) {
        this.sickNoteService = sickNoteService;
        this.sickNoteEventHandlerExtension = sickNoteEventHandlerExtension;
        this.clock = clock;
    }

    @Async
    @EventListener(ApplicationStartedEvent.class)
    void republishEvents() {

        LOG.info("Republishing all events with type sickNoteCreatedEvent");

        final LocalDate now = LocalDate.now(clock);

        final LocalDate startOfYear = now.withDayOfYear(1);
        final LocalDate endOfYear = startOfYear.with(lastDayOfYear());

        sickNoteService.getAllActiveByPeriod(startOfYear, endOfYear)
            .stream()
            .map(SickNoteCreatedEvent::of)
            .forEach(event -> {
                LOG.info("Publishing sickNoteCreatedEvent with id={} for personId={} with startDate={} and endDate={}", event.getSickNote().getId(), event.getSickNote().getPerson().getId(), event.getSickNote().getStartDate(), event.getSickNote().getEndDate());
                sickNoteEventHandlerExtension.on(event);
            });

        LOG.info("Republished all events with type=SickNoteCreatedEvent");
    }
}