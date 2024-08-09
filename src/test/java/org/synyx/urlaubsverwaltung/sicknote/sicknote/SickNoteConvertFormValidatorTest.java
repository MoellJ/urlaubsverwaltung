package org.synyx.urlaubsverwaltung.sicknote.sicknote;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link SickNoteConvertFormValidator}.
 */
class SickNoteConvertFormValidatorTest {

    private SickNoteConvertFormValidator validator;

    private Errors errors;

    @BeforeEach
    void setUp() {
        validator = new SickNoteConvertFormValidator();
        errors = mock(Errors.class);
    }

    @Test
    void ensureNullReasonIsNotValid() {

        final SickNoteConvertForm convertForm = new SickNoteConvertForm(SickNote.builder().build());
        convertForm.setReason(null);

        validator.validate(convertForm, errors);

        verify(errors).rejectValue("reason", "error.entry.mandatory");
    }

    @Test
    void ensureEmptyReasonIsNotValid() {

        final SickNoteConvertForm convertForm = new SickNoteConvertForm(SickNote.builder().build());
        convertForm.setReason("");

        validator.validate(convertForm, errors);

        verify(errors).rejectValue("reason", "error.entry.mandatory");
    }
}
