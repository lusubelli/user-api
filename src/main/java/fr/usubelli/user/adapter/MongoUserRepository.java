package fr.usubelli.user.adapter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.usubelli.user.dto.User;
import fr.usubelli.user.exception.UserAlreadyExistsException;
import fr.usubelli.user.exception.UserNotFoundException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoUserRepository {

    private final String host;
    private final String databaseName;
    private final String collection;
    private MongoCollection<User> userCollection;

    public MongoUserRepository(String host, String databaseName, String collection) {
        this.host = host;
        this.databaseName = databaseName;
        this.collection = collection;
    }

    private MongoCollection<User> userCollection() {
        if (userCollection == null) {
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            final MongoClient mongoClient = new MongoClient(host);
            final MongoDatabase database = mongoClient.getDatabase(databaseName);
            final MongoCollection<User> user = database.getCollection(collection, User.class);
            userCollection = user.withCodecRegistry(pojoCodecRegistry);
        }
        return userCollection;
    }

    public User createUser(User user) throws UserAlreadyExistsException {
        try {
            findUser(user.getEmail());
            throw new UserAlreadyExistsException();
        } catch (UserNotFoundException e) {
            System.out.println(String.format("Create user email [%s]", user.getEmail()));
            userCollection().insertOne(user);
            User a = user;
            try {
                a = findUser(user.getEmail());
                System.out.println(String.format("Created user email [%s] : \n %s", user.getEmail(), a));
            } catch (UserNotFoundException ex) {
                // TODO log
                ex.printStackTrace();
            }
            return a;
        }
    }

    public User findUser(String email) throws UserNotFoundException {
        System.out.println(String.format("Find user by email [%s]", email));
        final User user = userCollection().find(eq("email", email)).first();
        System.out.println(String.format("User by email [%s] : \n %s", email, user));
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public User updateUser(User user) throws UserNotFoundException {
        if(findUser(user.getEmail()) == null) {
            throw new UserNotFoundException();
        }
        System.out.println(String.format("Update user email [%s]", user.getEmail()));
        userCollection().replaceOne(eq("email", user.getEmail()), user);
        System.out.println(String.format("Updated user email [%s] : \n %s", user.getEmail(),
                findUser(user.getEmail())));
        return findUser(user.getEmail());
    }

}
