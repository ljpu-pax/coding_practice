import java.util.*;

public class PermissionResolver {
    // 用户直接对摄像头的权限
    private Map<String, Set<String>> userToCameras = new HashMap<>();
    // 用户所属的组
    private Map<String, Set<String>> userToGroups = new HashMap<>();
    // 组对摄像头的权限
    private Map<String, Set<String>> groupToCameras = new HashMap<>();
    // 组之间的继承关系
    private Map<String, Set<String>> groupToGroups = new HashMap<>();
    // 所有摄像头的集合
    private Set<String> allCameras = new HashSet<>();

    // 添加用户对摄像头的权限
    public void addUserCamera(String user, String camera) {
        userToCameras.computeIfAbsent(user, k -> new HashSet<>()).add(camera);
        allCameras.add(camera);
    }

    // 添加用户所属的组
    public void addUserGroup(String user, String group) {
        userToGroups.computeIfAbsent(user, k -> new HashSet<>()).add(group);
    }

    // 添加组对摄像头的权限
    public void addGroupCamera(String group, String camera) {
        groupToCameras.computeIfAbsent(group, k -> new HashSet<>()).add(camera);
        allCameras.add(camera);
    }

    // 添加组之间的继承关系
    public void addGroupGroup(String parent, String child) {
        groupToGroups.computeIfAbsent(child, k -> new HashSet<>()).add(parent);
    }

    // 获取用户的所有权限（包括继承）
    public Set<String> getUserPermissions(String user) {
        Set<String> permissions = new HashSet<>();
        // 添加用户直接对摄像头的权限
        permissions.addAll(userToCameras.getOrDefault(user, Collections.emptySet()));
        // 获取用户所属的所有组（包括继承）
        Set<String> allGroups = getAllGroups(userToGroups.getOrDefault(user, Collections.emptySet()));
        // 添加组对摄像头的权限
        for (String group : allGroups) {
            permissions.addAll(groupToCameras.getOrDefault(group, Collections.emptySet()));
        }
        return permissions;
    }

    // 获取所有组（包括继承）
    private Set<String> getAllGroups(Set<String> groups) {
        Set<String> allGroups = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>(groups);
        while (!stack.isEmpty()) {
            String group = stack.pop();
            if (allGroups.add(group)) {
                stack.addAll(groupToGroups.getOrDefault(group, Collections.emptySet()));
            }
        }
        return allGroups;
    }

    // 找出对所有摄像头有权限的用户
    public List<String> findUsersWithAllPermissions() {
        List<String> result = new ArrayList<>();
        Set<String> allUsers = new HashSet<>();
        allUsers.addAll(userToCameras.keySet());
        allUsers.addAll(userToGroups.keySet());
        for (String user : allUsers) {
            if (getUserPermissions(user).containsAll(allCameras)) {
                result.add(user);
            }
        }
        return result;
    }

    // 示例用法
    public static void main(String[] args) {
        PermissionResolver resolver = new PermissionResolver();
        resolver.addUserCamera("user_1", "camera_1");
        resolver.addUserGroup("user_1", "group_1");
        resolver.addGroupCamera("group_1", "camera_1");
        resolver.addGroupGroup("group_2", "group_1");
        resolver.addGroupCamera("group_2", "camera_2");

        List<String> users = resolver.findUsersWithAllPermissions();
        System.out.println("Users with all camera permissions: " + users);
    }
}

