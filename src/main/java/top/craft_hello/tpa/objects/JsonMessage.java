package top.craft_hello.tpa.objects;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.enums.ClickEventType;
import top.craft_hello.tpa.enums.HoverEventType;
import top.craft_hello.tpa.utils.MessageUtil;

public final class JsonMessage {
    private String jsonCommand;
    private String text;
    private String value;
    private final String[] colorCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"};
    private final String[] colors = {",\"color\":\"black\"", ",\"color\":\"dark_blue\"", ",\"color\":\"dark_green\"", ",\"color\":\"dark_aqua\"", ",\"color\":\"dark_red\"", ",\"color\":\"dark_purple\"", ",\"color\":\"gold\"", ",\"color\":\"gray\"", ",\"color\":\"dark_gray\"", ",\"color\":\"blue\"", ",\"color\":\"green\"", ",\"color\":\"aqua\"", ",\"color\":\"red\"", ",\"color\":\"light_purple\"", ",\"color\":\"yellow\"", ",\"color\":\"white\""};
    private final String[] formatCodes = {"&k", "&l", "&m", "&n", "&o"};
    private final String[] formats = {",\"obfuscated\":true", ",\"bold\":true", ",\"strikethrough\":true", ",\"underlined\":true", ",\"italic\":true"};

    public JsonMessage(@NotNull CommandSender target, @NotNull String text) {
        this.text = formatText(text);
        this.jsonCommand = "tellraw " + target.getName() + " [{\"text\":\"" + this.text;
    }

    public JsonMessage addText(){
        addText(" \"");
        return this;
    }


    public JsonMessage addText(@NotNull String text){
        this.text= formatText(text);
        this.jsonCommand = this.jsonCommand + "},{\"text\":\"" + this.text;
        return this;
    }

    public JsonMessage addInsertion(@NotNull String value){
        this.value = value;
        this.jsonCommand = this.jsonCommand + ",\"insertion\":\"" + this.value + "\"";
        return this;
    }

    public JsonMessage addClickEvent(@NotNull ClickEventType clickEventType, @NotNull String value){
        this.value = value;
        switch (clickEventType){
            case OPEN_URL:
                this.jsonCommand = this.jsonCommand + ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + this.value + "\"}";
                break;
            case OPEN_FILE:
                this.jsonCommand = this.jsonCommand + ",\"clickEvent\":{\"action\":\"open_file\",\"value\":\"" + this.value + "\"}";
                break;
            case RUN_COMMAND:
                this.jsonCommand = this.jsonCommand + ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + this.value + "\"}";
                break;
            case SUGGEST_COMMAND:
                this.jsonCommand = this.jsonCommand + ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + this.value + "\"}";
                break;
            case COPY_TO_CLIPBOARD:
                this.jsonCommand = this.jsonCommand + ",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"" + this.value + "\"}";
                break;
            default:
                break;
        }
        return this;
    }

    public JsonMessage addHoverEvent(@NotNull HoverEventType hoverEventType, @NotNull String value){
        this.value = MessageUtil.formatText(value);
        switch (hoverEventType){
            case SHOW_TEXT:
                this.jsonCommand = this.jsonCommand + ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + this.value + "\"}";
                break;
            case SHOW_ITEM:
                this.jsonCommand = this.jsonCommand + ",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"" + this.value + "\"}";
                break;
            case SHOW_ENTITY:
                this.jsonCommand = this.jsonCommand + ",\"hoverEvent\":{\"action\":\"show_entity\",\"value\":\"" + this.value + "\"}";
                break;
            default:
                break;
        }
        return this;
    }

    private String formatText(@NotNull String value) {
        int index = 0;
        for (String formatCode : formatCodes) {
            if(value.contains(formatCode)){
                value = value.replace(formatCode, "") + "\"" + formats[index];
            }
            index++;
        }
        index = 0;
        for (String colorCode : colorCodes) {
            if(value.contains(colorCode)){
                value = value.replace(colorCode, "") + colors[index];
                break;
            }
            index++;
        }
        return value;
    }

    @Override
    public String toString() {
        return jsonCommand + "}]";
    }
}
