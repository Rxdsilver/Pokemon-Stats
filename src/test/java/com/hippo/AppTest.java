package com.hippo;

import com.hippo.objects.Player;
import com.hippo.objects.Pokemon;
import com.hippo.objects.Team;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    // Test for getTeam method in App class
    public void testGetTeam() {
        String url = "https://rk9.gg/teamlist/public/WCS02wi0zpmUDdrwWkd1/9bMC6A6RpFDm3wD5GVDY";
        App app = new App();
        Team team = app.getTeam(url);
        Pokemon[] pokemons = team.getPokemons();

        assertEquals("Miraidon", pokemons[0].getName());
        assertEquals("Fairy", pokemons[0].getType());
        assertEquals("Hadron Engine", pokemons[0].getAbility());
        assertEquals("Choice Specs", pokemons[0].getItem());
        assertEquals("Electro Drift", pokemons[0].getMoves().get(0));
        assertEquals("Draco Meteor", pokemons[0].getMoves().get(1));
        assertEquals("Dazzling Gleam", pokemons[0].getMoves().get(2));
        assertEquals("Volt Switch", pokemons[0].getMoves().get(3));

        assertEquals("Ogerpon [Hearthflame Mask]", pokemons[1].getName());
        assertEquals("Fire", pokemons[1].getType());
        assertEquals("Mold Breaker", pokemons[1].getAbility());
        assertEquals("Hearthflame Mask", pokemons[1].getItem());
        assertEquals("Spiky Shield", pokemons[1].getMoves().get(0));
        assertEquals("Ivy Cudgel", pokemons[1].getMoves().get(1));
        assertEquals("Wood Hammer", pokemons[1].getMoves().get(2));
        assertEquals("Follow Me", pokemons[1].getMoves().get(3));

        assertEquals("Urshifu [Rapid Strike Style]", pokemons[2].getName());
        assertEquals("Water", pokemons[2].getType());
        assertEquals("Unseen Fist", pokemons[2].getAbility());
        assertEquals("Focus Sash", pokemons[2].getItem());
        assertEquals("Detect", pokemons[2].getMoves().get(0));
        assertEquals("Surging Strikes", pokemons[2].getMoves().get(1));
        assertEquals("Close Combat", pokemons[2].getMoves().get(2));
        assertEquals("Aqua Jet", pokemons[2].getMoves().get(3));
    }

    // Test for getPlayers method in App class
    public void testGetPlayers() {
        String url = "https://rk9.gg/roster/WCS02wi0zpmUDdrwWkd1";
        App app = new App();
        List<Player> players = app.getPlayers(url);

        assertEquals("Brendan", players.get(0).getFirstname());
        assertEquals("Zheng", players.get(0).getLastname());
        assertEquals("US", players.get(0).getCountry());
        assertEquals("Incineroar", players.get(0).getTeam().getPokemons()[0].getName());
        assertEquals("Bug", players.get(0).getTeam().getPokemons()[0].getType());
        assertEquals("Intimidate", players.get(0).getTeam().getPokemons()[0].getAbility());
        assertEquals("Safety Goggles", players.get(0).getTeam().getPokemons()[0].getItem());
        assertEquals("Fake Out", players.get(0).getTeam().getPokemons()[0].getMoves().get(0));
        assertEquals("Knock Off", players.get(0).getTeam().getPokemons()[0].getMoves().get(1));
        assertEquals("Parting Shot", players.get(0).getTeam().getPokemons()[0].getMoves().get(2));
        assertEquals("Flare Blitz", players.get(0).getTeam().getPokemons()[0].getMoves().get(3));
    }
}
