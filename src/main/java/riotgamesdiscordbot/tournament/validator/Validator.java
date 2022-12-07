package riotgamesdiscordbot.tournament.validator;

import riotgamesdiscordbot.tournament.validations.ValidationStatus;

public interface Validator {
    ValidationStatus validate();
}
