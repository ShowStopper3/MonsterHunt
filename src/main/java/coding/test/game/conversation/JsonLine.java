package coding.test.game.conversation;

import java.util.ArrayList;
import java.util.List;

import coding.test.game.characters.NonPlayingCharacter;
import coding.test.game.characters.Player;
import coding.test.game.menus.BaseMenu;
import coding.test.game.menus.MenuPojo;

public class JsonLine {
    private int id;
    private String playerPrompt;
    private String text;
    private PlayerType condition;
    private String conditionParameter;
    private List<Integer> responses;
    private ActionType action;

    public JsonLine(int id, String playerPrompt, String text, PlayerType condition, 
            String conditionParameter, List<Integer> responses, ActionType action) {
        this.id = id;
        this.playerPrompt = playerPrompt;
        this.text = text;
        this.condition = condition;
        this.conditionParameter = conditionParameter;
        this.responses = responses;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getPlayerPrompt() {
        return playerPrompt;
    }

    public PlayerType getCondition() {
        return condition;
    }

    public String getConditionParameter() {
        return conditionParameter;
    }

    public ActionType getAction() {
        return action;
    }

    public JsonLine display(NonPlayingCharacter npc, Player player, List<JsonLine> lines) {
        if (responses.size() == 0) {
            return null;
        }
        List<MenuPojo> responseList = new ArrayList<>();
        for (Integer responseNum : responses) { 
            JsonLine response = lines.get(responseNum);
            if (ConversationServices.matchesConditions(npc, player, response)) {
                responseList.add(new MenuPojo(response.getPlayerPrompt(), null));
            }
        }
        BaseMenu responseMenu = new BaseMenu();
        MenuPojo response = responseMenu.showMenu(responseList);
        for (int responseNum : responses) {
            JsonLine possibleResponse = lines.get(responseNum);
            if (possibleResponse.getPlayerPrompt().equals(response.getCommand())) {
                return possibleResponse;
            }
        }
        return null;
    }
}
