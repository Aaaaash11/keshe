import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class User {
    String username;
    TreeSet<String> following;

    public User(String username) {
        this.username = username;
        this.following = new TreeSet<>();
    }
}

class SocialNetwork {
    Map<String, User> users;  // 存储用户信息的映射（用户名到用户对象）
    Map<String, TreeSet<String>> followers;  // 存储关注关系的映射（被关注者用户名到关注者用户名的有序集合）

    public SocialNetwork() {
        this.users = new HashMap<>();
        this.followers = new HashMap<>();
    }

    // 添加关注关系
    public void addFollowRelation(String followerUsername, String followingUsername) {
        // 创建关注者和被关注者的用户对象，如果它们不存在的话
        if (!users.containsKey(followerUsername)) {
            users.put(followerUsername, new User(followerUsername));
        }
        if (!users.containsKey(followingUsername)) {
            users.put(followingUsername, new User(followingUsername));
        }

        // 将关注关系添加到关注者的关注列表中
        users.get(followerUsername).following.add(followingUsername);

        // 将关注者添加到被关注者的关注者列表中
        if (!followers.containsKey(followingUsername)) {
            followers.put(followingUsername, new TreeSet<>());
        }
        followers.get(followingUsername).add(followerUsername);
    }

    // 模拟信息传播路径
    public List<String> simulatePropagation(String sourceUsername) {
        List<String> propagationPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        // 获取源用户对象
        User sourceUser = users.get(sourceUsername);
        if (sourceUser == null) {
            System.out.println("Source user not found in the user list.");
            return propagationPath;
        }

        // 初始化队列，标记源用户为已访问
        queue.add(sourceUsername);
        visited.add(sourceUsername);

        // 使用广度优先搜索遍历关注关系图
        while (!queue.isEmpty()) {
            String currentUsername = queue.poll();
            propagationPath.add(currentUsername);

            // 获取当前用户对象
            User currentUser = users.get(currentUsername);
            if (currentUser != null) {
                // 将当前用户关注的用户加入队列
                for (String followingUsername : currentUser.following) {
                    if (!visited.contains(followingUsername)) {
                        queue.add(followingUsername);
                        visited.add(followingUsername);
                    }
                }
            }
        }

        return propagationPath;
    }
}

public class SocialNetworkSimulation {
    public static void main(String[] args) {
        SocialNetwork socialNetwork = new SocialNetwork();

        // 从文件读取用户关注关系信息
        try (BufferedReader br = new BufferedReader(new FileReader("E:/user_relationships.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 解析关注关系信息
                if (line.startsWith("关注关系：")) {
                    String[] tokens = line.substring(5).split(",");
                    for (String token : tokens) {
                        // 解析关注关系中的关注者和被关注者
                        String[] userRelation = token.split("->");
                        if (userRelation.length == 2) {
                            String followerUsername = userRelation[0].trim();
                            String followingUsername = userRelation[1].trim();
                            // 添加关注关系到社交网络对象
                            socialNetwork.addFollowRelation(followerUsername, followingUsername);
                        } else {
                            System.out.println("Invalid line in the relation file: " + token);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 提示用户选择源节点
        Scanner scanner = new Scanner(System.in);
        String sourceUsername = null;

        // 处理用户输入字符的情况
        while (sourceUsername == null) {
            System.out.print("Enter the source username: ");
            String userInput = scanner.nextLine();
            if (socialNetwork.users.containsKey(userInput.trim())) {
                sourceUsername = userInput.trim();
            } else {
                System.out.println("Invalid input. Please enter a valid username.");
            }
        }

        // 模拟信息传播路径
        List<String> propagationPath = socialNetwork.simulatePropagation(sourceUsername);

        // 打印输出信息传播路径
        System.out.println("Propagation Path:");
        for (String username : propagationPath) {
            System.out.println(username);
        }
    }
}
