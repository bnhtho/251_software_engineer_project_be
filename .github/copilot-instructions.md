Ngôn ngữ & cách trả lời
- Khi gợi ý code, viết comment hoặc giải thích, hãy dùng tiếng Việt.
- Tên class, method, biến, annotation, SQL… giữ nguyên tiếng Anh theo convention lập trình.

Dựa trên code thực tế của dự án
- Trước khi gợi ý, hãy ưu tiên đọc và làm theo cấu trúc, pattern và thư viện đã có trong repository hiện tại.
- Không tự thêm framework, thư viện hay kiến trúc mới nếu trong project chưa dùng, trừ khi người dùng yêu cầu rõ.
- Khi tạo file mới, tuân theo cấu trúc thư mục và cách đặt tên đã tồn tại trong dự án (ví dụ: controller/service/repository/entity cho Spring Boot).

Không đoán mò
- Không bịa ra entity, field, API, cấu hình hay business rule nếu không thấy trong code hoặc người dùng không nói tới.
- Nếu thông tin không rõ, thiếu, hoặc có mâu thuẫn giữa các file, hãy dừng lại và đề nghị người dùng làm rõ thay vì tự suy đoán.
- Nếu cần đặt giả định, phải ghi rõ trong comment TODO, ví dụ:
  // TODO: Giả định logic X vì chưa thấy yêu cầu rõ trong code. Cần xác nhận lại.

Không tự tạo tài liệu nếu không được yêu cầu
- Không tự sinh README, tài liệu API, tài liệu kiến trúc, migration script dài, diagram, hay comment mô tả chi tiết nếu người dùng không nhắc đến.
- Chỉ tạo tài liệu, mô tả, hoặc ghi chú dài khi người dùng yêu cầu rõ ràng trong prompt.

Phong cách code
- Giữ code đơn giản, dễ đọc, tách nhỏ hàm nếu logic phức tạp.
- Tuân thủ style hiện có trong dự án (ví dụ: Spring Boot 3 + JPA, REST controller → service → repository).
- Hạn chế thay đổi signature public API hiện có trừ khi người dùng yêu cầu.
