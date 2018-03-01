package coding.test.game.conversation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import coding.test.game.characters.NonPlayingCharacter;
import coding.test.game.characters.Player;
import coding.test.game.items.Item;
import coding.test.game.repository.ItemRepository;
import coding.test.game.repository.NpcRepository;
import coding.test.game.services.PlayerDeathException;
import coding.test.game.services.QueueServices;
import coding.test.game.services.RepositoryServices;

public class ConversationServices {
    private static NpcRepository npcRepository = NpcRepository.createRepo();
    private static ConversationServices instance = null;
    private Map<NonPlayingCharacter, List<JsonLine>> lines = new HashMap<NonPlayingCharacter, List<JsonLine>>();
    private static final Map<String, ActionType> ACTION_TYPE_MAP = new HashMap<>();
    private static final Map<String, PlayerType> CONDITION_TYPE_MAP = new HashMap<>();

    static {
        ACTION_TYPE_MAP.put("none", ActionType.NONE);
        ACTION_TYPE_MAP.put("attack", ActionType.ATTACK);
        ACTION_TYPE_MAP.put("give", ActionType.GIVE);
        ACTION_TYPE_MAP.put("take", ActionType.TAKE);
        CONDITION_TYPE_MAP.put("none", PlayerType.NONE);
        CONDITION_TYPE_MAP.put("ally", PlayerType.ALLY);
        CONDITION_TYPE_MAP.put("enemy", PlayerType.ENEMY);
        CONDITION_TYPE_MAP.put("level", PlayerType.LEVEL);
        CONDITION_TYPE_MAP.put("item", PlayerType.ITEM);
        CONDITION_TYPE_MAP.put("char type", PlayerType.CHAR_TYPE);
    }

    public ConversationServices() {
       load(); 
    } 

    public static ConversationServices getInstance() {
        if (instance == null) {
            instance = new ConversationServices();
        }
        return instance;
    }

    private void load() {
        String fileName = "json/npcs.json";
        JsonParser parser = new JsonParser();
        File f = new File(fileName);
        try {
            Reader reader = new FileReader(fileName);
            JsonObject json = parser.parse(reader).getAsJsonObject();
            json = json.get("npcs").getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                NonPlayingCharacter npc = npcRepository.getNpc(entry.getKey());
                JsonObject details = entry.getValue().getAsJsonObject();
                if (details.get("conversations") != null) {
                    JsonArray conversation = details.get("conversations").getAsJsonArray();
                    addConversation(npc, conversation);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addConversation(NonPlayingCharacter npc, JsonArray conversation) {
        List<JsonLine> start = new ArrayList<>();
        int i = 0;
        for (JsonElement entry : conversation) {
            JsonObject details = entry.getAsJsonObject();
            start.add(getLine(i++, conversation));
        }
        lines.put(npc, start);
    }

    private JsonLine getLine(int index, JsonArray conversation) {
        JsonObject line = conversation.get(index).getAsJsonObject();
        List<Integer> responses = new ArrayList<>();
        if (line.get("response") != null) {
            for (JsonElement i : line.get("response").getAsJsonArray()) {
                responses.add(i.getAsInt());
            }
        }
        String playerPrompt = line.get("player").getAsString();
        String text = line.get("text").getAsString();
        String[] con = line.get("condition").getAsString().split("=");
        PlayerType condition = CONDITION_TYPE_MAP.get(con[0]);
        String conditionParameter = (con.length == 1) ? "" : con[1];
        ActionType action = ACTION_TYPE_MAP.get(line.get("action").getAsString());
        return new JsonLine(index, playerPrompt, text, condition, conditionParameter, responses, action);
    }

    public void startConversation(NonPlayingCharacter npc, Player player) throws PlayerDeathException {
        List<JsonLine> conversation = null;
        //Workaround as <code>lines.get(npc)</code> is not working.
        Iterator it = lines.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<NonPlayingCharacter, List<JsonLine>> entry = (Map.Entry<NonPlayingCharacter, List<JsonLine>>) it.next();
            if (entry.getKey().equals(npc)) {
                conversation = entry.getValue();
            }
            it.remove();
        }
        if (conversation != null) {
            JsonLine start = null;
            for (JsonLine l : conversation) {
                if ("".equals(l.getPlayerPrompt()) && 
                            ConversationServices.matchesConditions(npc, player, l)) {
                    start = l;
                    break;
                }
            }
            if (start != null) {
                QueueServices.offer(start.getText());
                JsonLine response = start.display(npc, player, conversation);
                triggerAction(start, npc, player);
                while (response != null) {
                   QueueServices.offer(response.getText());
                   triggerAction(response, npc, player);
                   JsonLine temp_response = response.display(npc, player, conversation);
                   response = temp_response;
               }
            }
        }
    }

    private void triggerAction(JsonLine line, NonPlayingCharacter npc, Player player) throws PlayerDeathException {
        switch (line.getAction()) {
            case ATTACK:
                QueueServices.offer("\n" + npc.getName() + " is now attacking you!\n");
                player.attack(npc.getName());
                break;
            case TRADE:
               
                break;
        }     
    }

    public static boolean matchesConditions(NonPlayingCharacter npc, Player player, JsonLine line) {
        switch(line.getCondition()) {
            case ALLY:
                return npc.getAllies().contains(player.getCurrentCharacterType());
            case ENEMY:
                return npc.getEnemies().contains(player.getCurrentCharacterType());
            case LEVEL:
                int requiredLevel = Integer.parseInt(line.getConditionParameter());
                return player.getLevel() >= requiredLevel;
            case ITEM:
                ItemRepository itemRepo = RepositoryServices.getItemRepository();
                Item requiredItem = itemRepo.getItem(line.getConditionParameter());
                return player.hasItem(requiredItem);
            case CHAR_TYPE:
                String charType = line.getConditionParameter();
                return charType.equals(player.getCurrentCharacterType());
            default: // No condition
                return true;
        }
    }
}
