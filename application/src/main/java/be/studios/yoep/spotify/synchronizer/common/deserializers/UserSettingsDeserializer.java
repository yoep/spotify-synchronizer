package be.studios.yoep.spotify.synchronizer.common.deserializers;

import be.studios.yoep.spotify.synchronizer.settings.model.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class UserSettingsDeserializer extends JsonDeserializer<UserSettings> {
    private ObjectCodec codec;
    private TreeNode node;

    @Override
    public UserSettings deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        codec = parser.getCodec();
        node = codec.readTree(parser);

        Authentication authentication = getChild(UserSettings.AUTHENTICATION_PROPERTY, Authentication.class);
        Logging logging = getChild(UserSettings.LOGGING_PROPERTY, Logging.class);
        Synchronization synchronization = getChild(UserSettings.SYNCHRONISATION_PROPERTY, Synchronization.class);
        UserInterface userInterface = getChild(UserSettings.USER_INTERFACE_PROPERTY, UserInterface.class);

        return UserSettings.builder()
                .authentication(authentication)
                .logging(logging)
                .synchronization(synchronization)
                .userInterface(userInterface)
                .build();
    }

    private <T> T getChild(String property, Class<T> childType) throws IOException {
        TreeNode propertyNode = node.get(property);

        return codec.treeToValue(propertyNode, childType);
    }
}
