package com.hippo.objects;

import com.hippo.objects.rk9.Pokemon;

import java.util.List;

public class PokemonSearchCriteria {
    private DateRange dates;
    private List<Pokemon> pokemons;

    // Getters and Setters
    public DateRange getDates() {
        return dates;
    }

    public void setDates(DateRange dates) {
        this.dates = dates;
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    public static class DateRange {
        private String startDate;
        private String endDate;

        // Getters and Setters
        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}
