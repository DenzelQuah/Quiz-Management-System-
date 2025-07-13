import java.util.*;

public class QuizSystem2 {

    static UserNode userHead = null; // Stores registered users
    static QuizNode quizHead = null; // Linked list to store quizzes
    static QuizScoreNode scoreHead = null; // Linked list to store quiz scores

    public static void main(String[] args) {
        // Initialize a fixed tutor account
        addUser(new Tutor("tutor", "abc123", "Mathematics"));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Quiz System ---");
            System.out.println("1. Register as Student");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = getIntInput(scanner);

            switch (choice) {
                case 1 -> registerStudent(scanner);
                case 2 -> login(scanner);
                case 3 -> {
                    System.out.println("Exiting program...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Method to get valid integer input
    static int getIntInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    // Student registration
    static void registerStudent(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();

        // Check if username is already taken
        if (findUser(username) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.next();

        System.out.print("Enter grade level: ");
        String grade = scanner.next();

        addUser(new Student(username, password, grade));
        System.out.println("Student registered successfully!");
    }

    // Login method
    static void login(Scanner scanner) {
        System.out.print("Choose role:\n1. Tutor\n2. Student\nSelect: ");
        int role = getIntInput(scanner);
        scanner.nextLine(); // Consume newline

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = findUser(username);
        if (user != null && user.password.equals(password)) {
            if (role == 1 && user instanceof Tutor) {
                System.out.println("Login successful! Welcome, Tutor.");
                tutorMenu(scanner, (Tutor) user);
                return;
            } else if (role == 2 && user instanceof Student) {
                System.out.println("Login successful! Welcome, " + user.username);
                studentMenu(scanner, (Student) user);
                return;
            }
        }
        System.out.println("Invalid username or password.");
    }

    // Tutor menu
    static void tutorMenu(Scanner scanner, Tutor tutor) {
        QuestionStack questionStack = new QuestionStack();
        while (true) {
            System.out.println("\n--- Tutor Menu ---");
            System.out.println("1. Create Quiz");
            System.out.println("2. View Quizzes");
            System.out.println("3. Sort Quizzes by Difficulty");
            System.out.println("4. Delete Quiz");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            int choice = getIntInput(scanner);
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter quiz name: ");
                    String quizName = scanner.nextLine();
                    String difficulty;
                    while (true) {
                        System.out.print("Enter quiz difficulty (Easy/Medium/Hard): ");
                        difficulty = scanner.nextLine();
                        if (difficulty.equalsIgnoreCase("Easy") || difficulty.equalsIgnoreCase("Medium") || difficulty.equalsIgnoreCase("Hard")) {
                            break;
                        } else {
                            System.out.println("Invalid difficulty. Please enter Easy, Medium, or Hard.");
                        }
                    }
                    addQuiz(new QuizNode(quizName, difficulty));

                    while (true) {
                        System.out.println("\n--- Manage Questions ---");
                        System.out.println("1. Add Question");
                        System.out.println("2. Undo Last Action");
                        System.out.println("3. Redo Last Action");
                        System.out.println("4. Finish Quiz");
                        System.out.print("Choose an option: ");
                        int questionChoice = getIntInput(scanner);
                        scanner.nextLine(); // Consume newline

                        switch (questionChoice) {
                            case 1 -> {
                                System.out.print("Enter question text: ");
                                String questionText = scanner.nextLine();
                                System.out.print("Enter answer: ");
                                String answer = scanner.nextLine();
                                questionStack.push(new Question(questionText, answer));
                            }
                            case 2 -> questionStack.undo();
                            case 3 -> questionStack.redo();
                            case 4 -> {
                                QuizNode quiz = findQuiz(quizName);
                                if (quiz != null) {
                                    quiz.questions.addAll(questionStack.getAllQuestions());
                                    System.out.println("Quiz finalized!");
                                }
                                break;
                            }
                            default -> System.out.println("Invalid choice. Try again.");
                        }
                        if (questionChoice == 4) break;
                    }
                }
                case 2 -> viewQuizzes();
                case 3 -> sortQuizzesByDifficulty();
                case 4 -> {
                    System.out.print("Enter quiz name to delete: ");
                    String quizName = scanner.nextLine();
                    deleteQuiz(quizName);
                }
                case 5 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Student menu
    static void studentMenu(Scanner scanner, Student student) {
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. Search Quiz by Name");
            System.out.println("2. List All Quizzes (Sorted by Difficulty)");
            System.out.println("3. Take a Quiz");
            System.out.println("4. View Quiz Scores");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            int choice = getIntInput(scanner);
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter quiz name to search: ");
                    String keyword = scanner.nextLine();
                    searchQuizByName(keyword);
                }
                case 2 -> viewAndSortQuizzes();
                case 3 -> {
                    System.out.print("Enter quiz name to take: ");
                    String quizName = scanner.nextLine();
                    if (hasStudentTakenQuiz(student, quizName)) {
                        System.out.println("You have already taken this quiz.");
                    } else {
                        takeQuiz(scanner, student, quizName);
                    }
                }
                case 4 -> viewQuizScores(student);
                case 5 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Method for students to take a quiz
    static void takeQuiz(Scanner scanner, Student student, String quizName) {
        QuizNode quiz = findQuiz(quizName);
        if (quiz != null) {
            int score = 0;
            for (Question question : quiz.questions) {
                System.out.println(question.text);
                System.out.print("Your answer: ");
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase(question.answer)) {
                    score++;
                }
            }
            System.out.println("Quiz completed! Your score: " + score + "/" + quiz.questions.size());
            addQuizScore(new QuizScore(student.username, quizName, score));
        } else {
            System.out.println("Quiz not found!");
        }
    }

    // Method to check if a student has already taken a quiz
    static boolean hasStudentTakenQuiz(Student student, String quizName) {
        QuizScoreNode current = scoreHead;
        while (current != null) {
            if (current.score.studentUsername.equals(student.username) && current.score.quizName.equals(quizName)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Method to view quiz scores for a student
    static void viewQuizScores(Student student) {
        System.out.println("\n--- Quiz Scores ---");
        QuizScoreNode current = scoreHead;
        while (current != null) {
            if (current.score.studentUsername.equals(student.username)) {
                System.out.println("Quiz: " + current.score.quizName + " | Score: " + current.score.score);
            }
            current = current.next;
        }
    }

    // Add a user to the linked list
    static void addUser(User user) {
        UserNode newUser = new UserNode(user);
        newUser.next = userHead;
        userHead = newUser;
    }

    // Find a user by username
    static User findUser(String username) {
        UserNode current = userHead;
        while (current != null) {
            if (current.user.username.equals(username)) {
                return current.user;
            }
            current = current.next;
        }
        return null;
    }

    // Add a quiz to the linked list
    static void addQuiz(QuizNode quiz) {
        quiz.next = quizHead;
        quizHead = quiz;
    }

    // Find a quiz by name
    static QuizNode findQuiz(String name) {
        QuizNode current = quizHead;
        while (current != null) {
            if (current.name.equals(name)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // Delete a quiz by name
    static void deleteQuiz(String name) {
        if (quizHead == null) {
            System.out.println("No quizzes to delete.");
            return;
        }
        if (quizHead.name.equalsIgnoreCase(name)) {
            quizHead = quizHead.next;
            System.out.println("Quiz deleted successfully!");
            return;
        }
        QuizNode current = quizHead;
        while (current.next != null && !current.next.name.equalsIgnoreCase(name)) {
            current = current.next;
        }
        if (current.next == null) {
            System.out.println("Quiz not found.");
        } else {
            current.next = current.next.next;
            System.out.println("Quiz deleted successfully!");
        }
    }

    // View quizzes
    static void viewQuizzes() {
        if (quizHead == null) {
            System.out.println("No quizzes available.");
            return;
        }
        QuizNode current = quizHead;
        while (current != null) {
            System.out.println("Quiz Name: " + current.name + " | Difficulty: " + current.difficulty);
            current = current.next;
        }
    }

    // Sort quizzes by difficulty
    static void sortQuizzesByDifficulty() {
        if (quizHead == null || quizHead.next == null) {
            System.out.println("No quizzes to sort.");
            return;
        }

        // Bubble sort the linked list based on difficulty level
        boolean swapped;
        do {
            swapped = false;
            QuizNode current = quizHead;
            QuizNode prev = null;
            while (current != null && current.next != null) {
                if (difficultyRank(current.difficulty) > difficultyRank(current.next.difficulty)) {
                    // Swap nodes
                    QuizNode temp = current.next;
                    current.next = temp.next;
                    temp.next = current;
                    if (prev == null) {
                        quizHead = temp;
                    } else {
                        prev.next = temp;
                    }
                    swapped = true;
                }
                prev = current;
                current = current.next;
            }
        } while (swapped);

        System.out.println("Quizzes sorted by difficulty.");
    }

    private static int difficultyRank(String difficulty) {
        return switch (difficulty.toLowerCase()) {
            case "easy" -> 1;
            case "medium" -> 2;
            case "hard" -> 3;
            default -> 4;
        };
    }

    // Search quiz by name
    static void searchQuizByName(String name) {
        if (quizHead == null) {
            System.out.println("No quizzes available.");
            return;
        }
        QuizNode current = quizHead;
        boolean found = false;
        while (current != null) {
            if (current.name.equalsIgnoreCase(name)) {
                System.out.println("Quiz Found: " + current.name + " | Difficulty: " + current.difficulty);
                found = true;
            }
            current = current.next;
        }
        if (!found) {
            System.out.println("Quiz not found.");
        }
    }

    // View and sort quizzes
    static void viewAndSortQuizzes() {
        if (quizHead == null) {
            System.out.println("No quizzes available.");
            return;
        }

        // Sort quizzes by difficulty
        sortQuizzesByDifficulty();

        // Display sorted quizzes
        System.out.println("Quizzes sorted by difficulty:");
        QuizNode current = quizHead;
        while (current != null) {
            System.out.println("Quiz Name: " + current.name + " | Difficulty: " + current.difficulty);
            current = current.next;
        }
    }

    // Add a quiz score to the linked list
    static void addQuizScore(QuizScore score) {
        QuizScoreNode newScore = new QuizScoreNode(score);
        newScore.next = scoreHead;
        scoreHead = newScore;
    }
}

// Base user class
class User {
    String username;
    String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

// Tutor class
class Tutor extends User {
    String subject;

    Tutor(String username, String password, String subject) {
        super(username, password);
        this.subject = subject;
    }
}

// Student class
class Student extends User {
    String grade;

    Student(String username, String password, String grade) {
        super(username, password);
        this.grade = grade;
    }
}

// Question class
class Question {
    String text;
    String answer;

    Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }
}

// QuizNode class (Linked List Node)
class QuizNode {
    String name;
    String difficulty;
    LinkedList<Question> questions;
    QuizNode next;

    QuizNode(String name, String difficulty) {
        this.name = name;
        this.difficulty = difficulty;
        this.questions = new LinkedList<>();
        this.next = null;
    }
}

// UserNode class (Linked List Node)
class UserNode {
    User user;
    UserNode next;

    UserNode(User user) {
        this.user = user;
        this.next = null;
    }
}

// QuizScoreNode class (Linked List Node)
class QuizScoreNode {
    QuizScore score;
    QuizScoreNode next;

    QuizScoreNode(QuizScore score) {
        this.score = score;
        this.next = null;
    }
}

// Custom stack implementation using linked list
class QuestionStack {
    private StackNode top;
    private StackNode redoTop;

    // Push question onto the stack
    void push(Question question) {
        StackNode newNode = new StackNode(question);
        newNode.next = top;
        top = newNode;
        redoTop = null; // Clear redo stack on new action
        System.out.println("Question added.");
    }

    // Undo the last action
    void undo() {
        if (top == null) {
            System.out.println("No actions to undo.");
            return;
        }
        StackNode temp = top;
        top = top.next;
        temp.next = redoTop;
        redoTop = temp;
        System.out.println("Undo successful.");
    }

    // Redo the last undone action
    void redo() {
        if (redoTop == null) {
            System.out.println("No actions to redo.");
            return;
        }
        StackNode temp = redoTop;
        redoTop = redoTop.next;
        temp.next = top;
        top = temp;
        System.out.println("Redo successful.");
    }

    // Get all questions from the stack
    LinkedList<Question> getAllQuestions() {
        LinkedList<Question> questions = new LinkedList<>();
        StackNode current = top;
        while (current != null) {
            questions.add(current.question);
            current = current.next;
        }
        return questions;
    }
}

// Stack node class for custom stack implementation
class StackNode {
    Question question;
    StackNode next;

    StackNode(Question question) {
        this.question = question;
        this.next = null;
    }
}

// Class to store quiz scores
class QuizScore {
    String studentUsername;
    String quizName;
    int score;

    QuizScore(String studentUsername, String quizName, int score) {
        this.studentUsername = studentUsername;
        this.quizName = quizName;
        this.score = score;
    }
}