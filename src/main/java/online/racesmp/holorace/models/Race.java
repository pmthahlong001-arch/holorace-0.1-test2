package online.racesmp.holorace.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Race {

    private final String id;
    private final String name;
    private final String displayName;
    private final List<String> description;
    private final String iconMaterial;
    private final String iconTexture;
    private final List<SkillConfig> skills;

    public Race(String id, ConfigurationSection section) {
        this.id = id;
        this.name = section.getString("name", id);
        this.displayName = section.getString("display-name", name);
        this.description = section.getStringList("description");
        this.iconMaterial = section.getString("icon.material", "STONE");
        this.iconTexture = section.getString("icon.texture", null);
        this.skills = new ArrayList<>();

        // Load skills
        List<?> skillList = section.getList("skills");
        if (skillList != null) {
            for (Object obj : skillList) {
                if (obj instanceof java.util.Map<?, ?> map) {
                    // Ép kiểu an toàn sang Map<String, Object> để xử lý nội bộ dễ dàng hơn
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> safeMap = (java.util.Map<String, Object>) (java.util.Map<?, ?>) map;
                    skills.add(new SkillConfig(safeMap));
                }
            }
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public List<String> getDescription() { return description; }
    public String getIconMaterial() { return iconMaterial; }
    public String getIconTexture() { return iconTexture; }
    public List<SkillConfig> getSkills() { return skills; }

    // Inner class lưu config của từng skill trong file race
    public static class SkillConfig {
        private final String type;
        private final String trigger;
        private final java.util.Map<String, Object> raw; // ĐỔI THÀNH: Map<String, Object> để tránh lỗi capture

        public SkillConfig(java.util.Map<String, Object> map) {
            this.raw = map;
            
            // Ép kiểu Object về String, giải quyết triệt để lỗi dòng 54, 55 của bạn
            Object typeVal = map.get("type");
            this.type = (typeVal != null ? typeVal.toString() : "PASSIVE").toUpperCase();
            
            Object triggerVal = map.get("trigger");
            this.trigger = (triggerVal != null ? triggerVal.toString() : "ALWAYS").toUpperCase();
        }

        public String getType() { return type; }
        public String getTrigger() { return trigger; }

        public double getDouble(String key, double def) {
            Object val = raw.get(key);
            if (val == null) return def;
            try { return Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
        }

        public int getInt(String key, int def) {
            Object val = raw.get(key);
            if (val == null) return def;
            try { return Integer.parseInt(val.toString()); } catch (Exception e) { return def; }
        }

        public boolean getBoolean(String key, boolean def) {
            Object val = raw.get(key);
            if (val == null) return def;
            return Boolean.parseBoolean(val.toString());
        }

        // ĐỔI THÀNH: Trả về List<Map<String, ?>> để ăn khớp hoàn hảo với các Listener của bạn
        @SuppressWarnings("unchecked")
        public List<java.util.Map<String, ?>> getEffectList() {
            Object val = raw.get("effects");
            if (val instanceof List<?> list) {
                List<java.util.Map<String, ?>> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof java.util.Map<?, ?> m) {
                        // Ép kiểu hai bước cưỡng chế cấu trúc map bên trong danh hiệu ứng
                        result.add((java.util.Map<String, ?>) (java.util.Map<?, ?>) m);
                    }
                }
                return result;
            }
            return new ArrayList<>();
        }
    }
}
