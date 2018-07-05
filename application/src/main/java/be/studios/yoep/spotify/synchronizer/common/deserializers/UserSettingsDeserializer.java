package be.studios.yoep.spotify.synchronizer.common.deserializers;

import be.studios.yoep.spotify.synchronizer.settings.model.Authentication;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class UserSettingsDeserializer extends JsonDeserializer<UserSettings> {
    @Override
    public UserSettings deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = p.getCodec();
        TreeNode node = codec.readTree(p);
        TreeNode authenticationNode = node.get("authentication");
        Authentication authentication = codec.treeToValue(authenticationNode, Authentication.class);

        return null;
    }
}
