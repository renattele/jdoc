package jdoc.document.domain.change.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jdoc.document.domain.User;
import jdoc.document.domain.change.Change;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddUserChange.class, name = "user_add"),
        @JsonSubTypes.Type(value = AddMultipleUsersChange.class, name = "user_add_multiple"),
        @JsonSubTypes.Type(value = RemoveUserChange.class, name = "user_remove"),
        @JsonSubTypes.Type(value = RenameUserChange.class, name = "user_rename")
})
public interface UserChange extends Change<List<User>, UserChange> {
    @Override
    default UserChange reduce(Iterable<UserChange> changes) {
        var users = new ArrayList<User>();
        for (UserChange change : changes) {
            change.apply(users);
        }
        return new AddMultipleUsersChange(users);
    }
}
