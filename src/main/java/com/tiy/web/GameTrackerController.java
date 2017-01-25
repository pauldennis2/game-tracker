package com.tiy.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Dennis on 1/9/2017.
 */
@Controller
public class GameTrackerController {

    //Initializes to point to the specified DB in application.properties
    @Autowired
    GameRepository games;

    @Autowired
    UserRepository users;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(HttpSession session, Model model, String genre, Integer releaseYear) {
        if (session.getAttribute("user") != null) {
            model.addAttribute("user", session.getAttribute("user"));
        }
        User savedUser = (User)session.getAttribute("user");
        List<Game> gameList = new ArrayList<Game>();
        if (genre != null) {
            gameList = games.findByUserAndGenre(savedUser, genre);
        } else if (releaseYear != null) {
            gameList = games.findByUserAndReleaseYear(savedUser, releaseYear);
        } else {
            if (savedUser != null) {
                gameList = games.findByUser(savedUser);
            } else {
                Iterable<Game> allGames = games.findAll();
                for (Game game : allGames) {
                    gameList.add(game);
                }
            }
        }
        model.addAttribute("games", gameList);
        return "home";
    }

    @PostConstruct
    public void init() {
        if (users.count() == 0) {
            User user = new User();
            user.name = "Zach";
            user.password = "hunter2";
            users.save(user);
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String userName, String password) throws Exception {
        User user = users.findFirstByName(userName);
        if (user == null) {
            user = new User(userName, password);
            users.save(user);
        }
        else if (!password.equals(user.getPassword())) {
            throw new Exception("Incorrect password");
        }
        session.setAttribute("user", user);
        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(path = "/add-game", method = RequestMethod.POST)
    public String addGame(HttpSession session, String gameName, String gamePlatform, String gameGenre, int gameYear) {
        User user = (User) session.getAttribute("user");
        Game game = new Game(gameName, gamePlatform, gameGenre, gameYear, user);
        games.save(game);
        return "redirect:/";
    }

    @RequestMapping(path = "/searchByName", method = RequestMethod.GET)
    public String queryGamesByName(Model model, String search) {
        System.out.println("Searching by ..." + search);
        List<Game> gameList = games.findByNameStartsWith(search);
        model.addAttribute("games", gameList);
        return "home";
    }

    @RequestMapping(path = "/delete", method = RequestMethod.GET)
    public String deleteGame(Model model, Integer gameId) {
        if (gameId != null) {
            games.delete(gameId);
        }

        return "redirect:/";
    }

    @RequestMapping(path = "/modify", method = RequestMethod.GET)
    public String modify(Model model, Integer gameId) {
        if (gameId != null) {
            Game game = games.findOne(gameId);
            game.name = " ** " + game.name;
            games.save(game);
        }

        return "redirect:/";
    }


}
