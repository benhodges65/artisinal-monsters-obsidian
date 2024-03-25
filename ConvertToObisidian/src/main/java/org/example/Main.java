package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final String INPUT_FILE = "src/main/resources/monsters.json"; // Replace with your actual file path
    private static final String OUTPUT_FILE = "src/main/resources/converted_monsters.json"; // Replace with your desired
                                                                                            // output file path

    public static void main(String[] args) {
        List<Map<String, Object>> monsters = readMonstersFromFile();
        List<Map<String, Object>> convertedMonsters = convertMonsterFormat(monsters);
        writeMonstersToFile(convertedMonsters);
    }

    private static List<Map<String, Object>> readMonstersFromFile() {
        List<Map<String, Object>> monsters = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray jsonArray = new JSONArray(jsonString.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject monsterJson = jsonArray.getJSONObject(i);
                Map<String, Object> monsterMap = convertJsonObjectToMap(monsterJson);
                monsters.add(monsterMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return monsters;
    }

    private static Map<String, Object> convertJsonObjectToMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof String) {
                map.put(key, value.toString());
            } else if (value instanceof JSONObject) {
                map.put(key, convertJsonObjectToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {

            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    private static List<Map<String, Object>> convertMonsterFormat(List<Map<String, Object>> monsters) {
        List<Map<String, Object>> convertedMonsters = new ArrayList<>();
        for (Map<String, Object> monster : monsters) {
            Map<String, Object> convertedMonster = new HashMap<>();
            convertedMonster.put("layout", "Basic 5e Layout");
            convertedMonster.put("name", monster.get("name") != null ? monster.get("name") : null); // Combine name and
                                                                                                    // title
            convertedMonster.put("size",
                    monster.get("size") != null ? capitalizeFirstLetter(monster.get("size").toString().toLowerCase()) : null);

            convertedMonster.put("type",
                    monster.get("type") != null ? capitalizeFirstLetter(monster.get("type").toString().toLowerCase()) : "");
            convertedMonster.put("subtype", monster.get("subtype") != null ? monster.get("subtype") : "");

            convertedMonster.put("alignment",
                    monster.get("alignment") != null ? monster.get("alignment").toString().toLowerCase() : null);
            convertedMonster.put("ac", monster.get("armor_class") != null ? monster.get("armor_class") : null);
            convertedMonster.put("armor_desc", monster.get("armor_desc") != null ? monster.get("armor_desc") : null);
            convertedMonster.put("hp", monster.get("hit_points") != null ? monster.get("hit_points") : null);
            convertedMonster.put("hit_dice", monster.get("hit_dice") != null ? monster.get("hit_dice") : null);

            convertedMonster.put("speed",
                    monster.get("speed_json") != null ? parseSpeed(monster.get("speed_json").toString()) : null);

            List<Integer> stats = new ArrayList<>();
            stats.add(monster.get("strength") != null ? (int) monster.get("strength") : 10);
            stats.add(monster.get("dexterity") != null ? (int) monster.get("dexterity") : 10);
            stats.add(monster.get("constitution") != null ? (int) monster.get("constitution") : 10);
            stats.add(monster.get("intelligence") != null ? (int) monster.get("intelligence") : 10);
            stats.add(monster.get("wisdom") != null ? (int) monster.get("wisdom") : 10);
            stats.add(monster.get("charisma") != null ? (int) monster.get("charisma") : 10);
            convertedMonster.put("stats", stats);

            convertedMonster.put("saves", parseSavingThrows(monster));

            convertedMonster.put("skillsaves",
                    parseSkills(monster.get("skills_json") != null ? monster.get("skills_json").toString() : null));

            convertedMonster.put("damage_vulnerabilities",
                    monster.get("damage_vulnerabilities") != null ? monster.get("damage_vulnerabilities") : null);
            convertedMonster.put("damage_resistances",
                    monster.get("damage_resistances") != null ? monster.get("damage_resistances") : null);
            convertedMonster.put("damage_immunities",
                    monster.get("damage_immunities") != null ? monster.get("damage_immunities") : null);
            convertedMonster.put("condition_immunities",
                    monster.get("condition_immunities") != null ? monster.get("condition_immunities") : null);

            convertedMonster.put("senses", monster.get("senses") != null ? monster.get("senses") : null);
            convertedMonster.put("languages", monster.get("languages") != null ? monster.get("languages") : null);

            convertedMonster.put("cr",
                    monster.get("challenge_rating") != null
                            ? Double.valueOf(monster.get("challenge_rating").toString())
                            : null);

            convertedMonster.put("actions",
                    monster.get("actions_json") != null && !monster.get("actions_json").toString().equals("null")
                            ? parseActions(monster.get("actions_json").toString())
                            : new ArrayList<>());

            convertedMonster.put("bonus_actions",
                    monster.get("bonus_actions_json") != null && !monster.get("bonus_actions_json").toString().equals("null")
                            ? parseActions(monster.get("bonus_actions_json").toString())
                            : new ArrayList<>());

            convertedMonster.put("reactions",
                    monster.get("reactions_json") != null && !monster.get("reactions_json").toString().equals("null")
                            ? parseActions(monster.get("reactions_json").toString())
                            : new ArrayList<>());

            convertedMonster.put("legendary_actions",
                    monster.get("legendary_actions_json") != null
                            && !monster.get("legendary_actions_json").toString().equals("null")
                                    ? parseActions(monster.get("legendary_actions_json").toString())
                                    : new ArrayList<>());

            convertedMonster.put("traits",
                    monster.get("legendary_actions_json") != null
                            && !monster.get("special_abilities_json").toString().equals("null")
                                    ? parseActions(monster.get("special_abilities_json").toString())
                                    : new ArrayList<>());

            convertedMonster.put("source",
                    monster.get("document__title") != null ? monster.get("document__title") : null);

            convertedMonsters.add(convertedMonster);
        }
        return convertedMonsters;
    }

    private static void writeMonstersToFile(List<Map<String, Object>> convertedMonsters) {
        try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            JSONArray jsonArray = new JSONArray(convertedMonsters);
            jsonArray.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parseSpeed(String speedJson) {
        try {
            // Parse speedJson using org.json library
            JSONObject speedObj = new JSONObject(speedJson);
            StringBuilder speedBuilder = new StringBuilder();
            for (Iterator<String> keys = speedObj.keys(); keys.hasNext();) {
                String movementType = keys.next();
                int speed = speedObj.getInt(movementType);

                // Capitalize the first letter of movementType
                movementType = movementType.substring(0, 1).toUpperCase() + movementType.substring(1);

                speedBuilder.append(movementType).append(" ").append(speed).append(" ft., ");
            }
            return speedBuilder.substring(0, speedBuilder.length() - 2); // Remove trailing comma and space
        } catch (JSONException e) {
            // Handle potential parsing errors
            return "—";
        }
    }

    private static Map<String, Integer> parseSavingThrows(Map<String, Object> savesMap) {
        Map<String, Integer> saves = new HashMap<>();
        String[] stats = { "strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma" };
        for (String stat : stats) {
            String saveKey = stat + "_save";
            Object saveValue = savesMap.get(saveKey);
            if (saveValue instanceof Integer) {
                saves.put(stat, (Integer) saveValue);
            }
        }
        return saves;
    }

    private static Map<String, Integer> parseSkills(String skillsJson) throws JSONException {
        try {
            Map<String, Integer> skills = new HashMap<>();
            if (skillsJson != null && !skillsJson.isEmpty()) {
                JSONObject skillsObj = new JSONObject(skillsJson);
                for (Iterator<String> keys = skillsObj.keys(); keys.hasNext();) {
                    String skillName = keys.next();
                    int skillBonus = skillsObj.getInt(skillName);
                    skills.put(skillName, skillBonus);
                }
            }
            return skills;
        } catch (JSONException e) {
            return new HashMap<>();
        }
    }

    private static List<Map<String, String>> parseActions(String actionsJson) throws JSONException {
        try {
            List<Map<String, String>> actions = new ArrayList<>();
            if (actionsJson != null && !actionsJson.isEmpty()) {
                JSONArray actionsArray = new JSONArray(actionsJson);
                for (int i = 0; i < actionsArray.length(); i++) {
                    JSONObject actionObj = actionsArray.getJSONObject(i);
                    Map<String, String> action = new HashMap<>();
                    action.put("name", actionObj.getString("name"));
                    action.put("desc", getDescription(actionObj));
                    actions.add(action);
                }
            }
            return actions;
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    private static String getDescription(JSONObject jsonObject) {
        if (jsonObject.has("desc")) {
            return jsonObject.getString("desc");
        } else {
            return jsonObject.getString("description");
        }
    }

    private static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}