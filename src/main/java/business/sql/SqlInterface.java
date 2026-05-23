package business.sql;

import java.util.List; // Dùng List thay vì ArrayList

public interface SqlInterface<T> {
    public int insert(T t);
    
    public int update(T t);
    
    public int delete(String id); // Lưu ý: Bảng khóa kép sẽ cần xử lý riêng
    
    public List<T> selectAll();
    
    public T selectById(String id);
    
    // Hàm bổ sung để bà Quỳnh làm thanh tìm kiếm
    public List<T> selectByCondition(String condition);
}