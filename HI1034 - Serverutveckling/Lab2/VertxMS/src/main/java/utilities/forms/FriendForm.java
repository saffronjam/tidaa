package utilities.forms;

import com.google.gson.Gson;

public record FriendForm(String token, Long friendId) {
    public String toJson() {
        return new Gson().toJson(this, FriendForm.class);
    }
}
