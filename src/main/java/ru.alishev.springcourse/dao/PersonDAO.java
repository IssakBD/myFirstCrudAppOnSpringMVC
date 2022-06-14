package ru.alishev.springcourse.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.alishev.springcourse.models.Person;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PersonDAO {
    private static int PEOPLE_COUNT;
    //private List<Person> people = new ArrayList<>();;

//    private static final String URL = "jdbc:postgresql://localhost:5432/first_db";
//    private static final String USERNAME = "postgres";
//    private static final String PASSWORD = "postgres";
//
//    private static Connection connection;
//    static{
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    { //block of initialization
//
//        people.add(new Person(++PEOPLE_COUNT, "Tom", 24, "tom@gmail.com"));
//        people.add(new Person(++PEOPLE_COUNT, "Bob", 25, "bob@gmail.com"));
//        people.add(new Person(++PEOPLE_COUNT, "Mike", 52, "mike@gmail.com"));
//        people.add(new Person(++PEOPLE_COUNT, "Katy", 42, "katy@gmail.com"));
//    }
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<Person> index(){
//        List<Person> people = new ArrayList<>();
//
//        try {
//            Statement statement = connection.createStatement();
//            String SQL = "SELECT * FROM Person";
//            ResultSet resultSet = statement.executeQuery(SQL);
//
//            while(resultSet.next()){
//                Person person = new Person();
//
//                person.setId(resultSet.getInt("id"));
//                person.setName(resultSet.getString("name"));
//                person.setEmail(resultSet.getString("email"));
//                person.setAge(resultSet.getInt("age"));
//
//                people.add(person);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return people;
        return jdbcTemplate.query("SELECT * FROM PERSON", new BeanPropertyRowMapper<>(Person.class));
    }

    public Optional<Person> show(String email){
        return jdbcTemplate.query("SELECT * FROM Person WHERE email=?", new Object[]{email}, new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }
    public Person show(int id){
         //return people.stream().filter(person -> person.getId() == id).findAny().orElse(null);

//        Person person = null;
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Person WHERE id = ?");
//            preparedStatement.setInt(1, id);
//
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            resultSet.next();
//
//            person = new Person();
//
//            person.setId(resultSet.getInt("id"));
//            person.setName(resultSet.getString("name"));
//            person.setEmail(resultSet.getString("email"));
//            person.setAge(resultSet.getInt("age"));
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        return jdbcTemplate.query("SELECT * FROM Person WHERE id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
    }

    public void save(Person person){
//        person.setId(++PEOPLE_COUNT);
//        people.add(person);

//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Person VALUES (1, ?, ?, ?)");
//            preparedStatement.setString(1, person.getName());
//            preparedStatement.setInt(2, person.getAge());
//            preparedStatement.setString(3, person.getEmail());
//
//            preparedStatement.executeUpdate();
////            Statement statement = connection.createStatement();
////            String SQL = "INSERT INTO Person VALUES(" + 1 + ", '" + person.getName() + "'," +  + person.getAge() + ",'" + person.getEmail() + "')";
////            statement.executeUpdate(SQL);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        jdbcTemplate.update("INSERT INTO Person(name, age, email, address) Values(?, ?, ?, ?)", person.getName(), person.getAge(), person.getEmail(), person.getAddress());
    }
    public void update(int id, Person updatedPerson){
//        Person personToBeUpdated = show(id);
//
//        personToBeUpdated.setName(updatedPerson.getName());
//        personToBeUpdated.setAge(updatedPerson.getAge());
//        personToBeUpdated.setEmail(updatedPerson.getEmail());

//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Person SET name = ?, age = ?, email = ? WHERE id = ?");
//            preparedStatement.setString(1, updatedPerson.getName());
//            preparedStatement.setInt(2, updatedPerson.getAge());
//            preparedStatement.setString(3, updatedPerson.getEmail());
//            preparedStatement.setInt(4, id);
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    jdbcTemplate.update("UPDATE Person SET name=?, age=?, email=?, address=? WHERE id=?", updatedPerson.getName(), updatedPerson.getAge(), updatedPerson.getEmail(), updatedPerson.getAddress(), id);
    }
    public void delete(int id){
        //people.removeIf(p -> p.getId() == id);

//        PreparedStatement preparedStatement = null;
//        try {
//            preparedStatement = connection.prepareStatement("DELETE FROM Person WHERE id = ?");
//            preparedStatement.setInt(1, id);
//
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        jdbcTemplate.update("DELETE FROM Person WHERE id=?", id);
    }

    ////////////////////
    //////Testing batch insert
    ////////////////////

    public void testMultipleUpdate(){
        List<Person> people = create1000People();

        long before = System.currentTimeMillis();
        for (Person person : people) {
            jdbcTemplate.update("INSERT INTO Person Values(?, ?, ?, ?)", person.getId(), person.getName(), person.getAge(), person.getEmail());
        }
        long after = System.currentTimeMillis();
        System.out.println("Time" + (after - before));
    }
    public void testBatchUpdate(){
        List<Person> people = create1000People();

        long before = System.currentTimeMillis();

        jdbcTemplate.batchUpdate("INSERT INTO Person VALUES (?,?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, people.get(i).getId());
                preparedStatement.setString(2, people.get(i).getName());
                preparedStatement.setInt(3, people.get(i).getAge());
                preparedStatement.setString(4, people.get(i).getEmail());
            }

            @Override
            public int getBatchSize() {
                return people.size();
            }
        });
        long after = System.currentTimeMillis();
        System.out.println("Time" + (after - before));
    }

    private List<Person> create1000People() {
        List<Person> people = new ArrayList<>();
        for(int i = 0; i < 1000; i++){
            people.add(new Person(i, "Name" + i, 30, "test"+i+"@mail.ru", "Abaya " + i));
        }

        return people;
    }
}
