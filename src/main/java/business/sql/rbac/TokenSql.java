package business.sql.rbac;

import business.sql.SqlInterface;
import common.db.DatabaseConnection;
import model.account.Token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Lớp quản lý các thao tác Database cho bảng TOKENS (Oracle). Thực hiện cơ chế
 * xác thực phiên làm việc (Session Validation).
 *
 * * @author TanVi
 */
public class TokenSql implements SqlInterface<Token> {

    public static TokenSql getInstance() {
        return new TokenSql();
    }

    /**
     * Kiểm tra nhanh một Token có còn hợp lệ để sử dụng hay không. Thường dùng
     * cho các Filter hoặc Timer kiểm tra phiên làm việc ngầm.
     *
     * * @param tokenValue Chuỗi Token cần kiểm tra
     * @return true nếu Token còn hạn, chưa bị xóa; false nếu ngược lại
     */
    public boolean isTokenValid(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM TOKENS "
                + "WHERE TOKEN_VALUE = ? "
                + "AND IS_DELETED = 0 "
                + "AND EXPIRY_DATE > SYSTIMESTAMP";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, tokenValue);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi nghiêm trọng tại TokenSql.isTokenValid: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tìm và trả về đối tượng Token đầy đủ nếu nó còn hiệu lực. Check thêm điều
     * kiện IS_REVOKED (đã bị thu hồi chủ động).
     */
    public Token findValidToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM TOKENS "
                + "WHERE TOKEN_VALUE = ? "
                + "AND IS_REVOKED = 0 "
                + "AND IS_DELETED = 0 "
                + "AND EXPIRY_DATE > SYSTIMESTAMP";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, tokenValue);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapToken(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tại TokenSql.findValidToken: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insert(Token t) {
        String sql = "INSERT INTO TOKENS (TOKEN_ID, ACCOUNT_ID, TOKEN_VALUE, EXPIRY_DATE, CREATED_AT, IS_REVOKED, IS_DELETED) "
                + "VALUES (?, ?, ?, ?, SYSTIMESTAMP, 0, 0)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            String tokenId = (t.getTokenId() == null || t.getTokenId().isBlank())
                    ? UUID.randomUUID().toString()
                    : t.getTokenId();

            pst.setString(1, tokenId);
            pst.setString(2, t.getAccountId());
            pst.setString(3, t.getTokenValue());
            pst.setTimestamp(4, t.getExpiryDate());

            return pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi Insert Token: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Dọn dẹp Database: Xóa cứng các Token đã hết hạn hoặc đã bị hủy. Giúp bảng
     * TOKENS luôn gọn nhẹ.
     */
    public int deleteExpiredTokens() {
        String sql = "DELETE FROM TOKENS "
                + "WHERE EXPIRY_DATE < SYSTIMESTAMP "
                + "OR IS_REVOKED = 1 "
                + "OR IS_DELETED = 1";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            return pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi dọn dẹp Token hết hạn: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int update(Token t) {
        String sql = "UPDATE TOKENS SET ACCOUNT_ID = ?, TOKEN_VALUE = ?, EXPIRY_DATE = ?, IS_REVOKED = ?, IS_DELETED = ? "
                + "WHERE TOKEN_ID = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, t.getAccountId());
            pst.setString(2, t.getTokenValue());
            pst.setTimestamp(3, t.getExpiryDate());
            pst.setInt(4, t.getIsRevoked());
            pst.setInt(5, t.getIsDeleted());
            pst.setString(6, t.getTokenId());

            return pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi Update Token ID " + t.getTokenId() + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(String id) {
        // Thực hiện Soft Delete (Xóa mềm) để giữ lại dữ liệu đối soát
        String sql = "UPDATE TOKENS SET IS_DELETED = 1 WHERE TOKEN_ID = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi Soft Delete Token ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Token selectById(String id) {
        String sql = "SELECT * FROM TOKENS WHERE TOKEN_ID = ? AND IS_DELETED = 0";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapToken(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SelectById Token: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Token> selectAll() {
        ArrayList<Token> list = new ArrayList<>();
        String sql = "SELECT * FROM TOKENS WHERE IS_DELETED = 0 ORDER BY CREATED_AT DESC";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(mapToken(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SelectAll Token: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Token> selectByCondition(String condition) {
        ArrayList<Token> list = new ArrayList<>();
        String sql = "SELECT * FROM TOKENS WHERE " + condition;
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(mapToken(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SelectByCondition Token: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Map dữ liệu từ ResultSet sang Object Token. Có xử lý tránh lỗi khi cột
     * không tồn tại trong kết quả truy vấn.
     */
    private Token mapToken(ResultSet rs) throws SQLException {
        Token t = new Token();
        t.setTokenId(rs.getString("TOKEN_ID"));
        t.setAccountId(rs.getString("ACCOUNT_ID"));
        t.setTokenValue(rs.getString("TOKEN_VALUE"));
        t.setExpiryDate(rs.getTimestamp("EXPIRY_DATE"));

        // Sử dụng try-catch để an toàn nếu cấu trúc bảng thay đổi nhẹ
        try {
            t.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        } catch (Exception ignored) {
        }
        try {
            t.setIsRevoked(rs.getInt("IS_REVOKED"));
        } catch (Exception ignored) {
        }
        try {
            t.setIsDeleted(rs.getInt("IS_DELETED"));
        } catch (Exception ignored) {
        }

        return t;
    }

    /**
     * Thu hồi hiệu lực của một Token cụ thể (thường dùng khi Log out).
     * Chuyển trạng thái IS_REVOKED thành 1.
     * * @param currentToken Chuỗi token cần thu hồi
     * @return số dòng bị ảnh hưởng (1 nếu thành công)
     */
    public int revokeToken(String currentToken) {
        if (currentToken == null || currentToken.isBlank()) {
            return 0;
        }

        String sql = "UPDATE TOKENS SET IS_REVOKED = 1 "
                + "WHERE TOKEN_VALUE = ? AND IS_DELETED = 0";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, currentToken);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                System.out.println("[TOKEN_REVOKE] Đã thu hồi thành công token: " + currentToken);
            }
            return rows;

        } catch (SQLException e) {
            System.err.println("Lỗi nghiêm trọng tại TokenSql.revokeToken: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
