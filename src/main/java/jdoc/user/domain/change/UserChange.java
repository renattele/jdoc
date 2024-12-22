package jdoc.user.domain.change;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jdoc.user.domain.User;
import jdoc.core.domain.change.Change;

import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReplaceUsersChange.class, name = "user_replace"),
        @JsonSubTypes.Type(value = RemoveUserChange.class, name = "user_remove"),
        @JsonSubTypes.Type(value = PutUserChange.class, name = "user_put")
})
public interface UserChange extends Change<Map<String, User>, UserChange> {
    @Override
    default UserChange reduce(UserChange change) {
        var users = new HashMap<String, User>();
        change.apply(users);
        return new ReplaceUsersChange(users);
    }
}
