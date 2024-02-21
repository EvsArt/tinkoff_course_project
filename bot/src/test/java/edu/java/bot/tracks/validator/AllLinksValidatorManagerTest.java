package edu.java.bot.tracks.validator;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class AllLinksValidatorManagerTest {

    @Mock LinkValidator linkValidator;

    @Test
    void validateLink() {
        String legalLink = "legal";
        String illegalLink = "illegal";

        List<LinkValidator> validatorList = new ArrayList<>(1);
        LinkValidator linkValidator = Mockito.mock(LinkValidator.class);
        Mockito.doReturn(true).when(linkValidator).validate(legalLink);
        Mockito.doReturn(false).when(linkValidator).validate(illegalLink);
        validatorList.add(linkValidator);

        AllLinksValidatorManager manager = new AllLinksValidatorManager(validatorList);

        boolean shouldBeTrue = manager.validateLink(legalLink);
        boolean shouldBeFalse = manager.validateLink(illegalLink);

        assertThat(shouldBeTrue).isTrue();
        assertThat(shouldBeFalse).isFalse();
    }
}
