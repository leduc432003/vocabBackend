# VocabMaster - Ứng Dụng Học Từ Vựng Tiếng Anh

Ứng dụng học từ vựng tiếng Anh hiện đại với **Spring Boot** backend và **React + TailwindCSS** frontend.

## ✨ Tính năng

### 🎯 Chức năng chính
- **Flashcards**: Học từ vựng với thẻ lật tương tác, hiệu ứng 3D mượt mà
- **Quiz**: Kiểm tra kiến thức với câu hỏi trắc nghiệm
- **Danh sách từ vựng**: Xem, tìm kiếm và quản lý tất cả từ vựng
- **Phát âm**: Text-to-Speech tích hợp để nghe phát âm chuẩn
- **Theo dõi tiến độ**: Thống kê chi tiết về quá trình học tập

### 🎨 Giao diện
- Dark/Light mode
- Responsive design
- Gradient và animations đẹp mắt
- TailwindCSS styling

### 📊 Thống kê
- Tổng số từ vựng
- Số từ đã học
- Streak days (ngày học liên tiếp)
- Thời gian học tập
- Kết quả quiz

## 🏗️ Cấu trúc dự án

```
vocab-app/
├── backend/          # Spring Boot REST API
│   ├── src/
│   │   └── main/
│   │       ├── java/com/vocabapp/
│   │       │   ├── controller/     # REST Controllers
│   │       │   ├── model/          # Entity models
│   │       │   ├── repository/     # JPA Repositories
│   │       │   ├── service/        # Business logic
│   │       │   └── config/         # Configuration
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
│
└── frontend/         # React + TailwindCSS
    ├── src/
    │   ├── components/    # React components
    │   ├── services/      # API services
    │   ├── App.tsx        # Main app
    │   └── index.css      # TailwindCSS styles
    └── package.json
```

## 🚀 Cài đặt và Chạy

### Yêu cầu
- **Java**: JDK 17 hoặc cao hơn
- **Maven**: 3.6+
- **Node.js**: 16+ (khuyến nghị 18+)
- **npm**: 8+

### Backend (Spring Boot)

1. Di chuyển vào thư mục backend:
```bash
cd backend
```

2. Chạy ứng dụng:
```bash
mvn spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

**API Endpoints:**
- `GET /api/vocabulary` - Lấy tất cả từ vựng
- `GET /api/vocabulary/{id}` - Lấy từ vựng theo ID
- `GET /api/vocabulary/search?keyword=...` - Tìm kiếm từ vựng
- `GET /api/vocabulary/quiz?limit=10` - Lấy từ vựng cho quiz
- `POST /api/vocabulary` - Thêm từ vựng mới
- `PUT /api/vocabulary/{id}` - Cập nhật từ vựng
- `PATCH /api/vocabulary/{id}/learned?learned=true` - Đánh dấu đã học
- `DELETE /api/vocabulary/{id}` - Xóa từ vựng
- `GET /api/progress` - Lấy tiến độ học tập
- `POST /api/progress/quiz-result` - Ghi nhận kết quả quiz


### Frontend (React)

1. Di chuyển vào thư mục frontend:
```bash
cd frontend
```

2. Cài đặt dependencies:
```bash
npm install
```

3. Chạy development server:
```bash
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:5173`

## 📱 Sử dụng

1. **Khởi động Backend** trước (port 8080)
2. **Khởi động Frontend** (port 5173)
3. Mở trình duyệt tại `http://localhost:5173`

### Các chế độ học:

#### 📚 Flashcards
- Nhấn vào thẻ để lật và xem nghĩa
- Sử dụng nút phát âm để nghe từ
- Đánh giá độ khó: Khó (đỏ), Trung bình (vàng), Dễ (xanh)
- Từ được đánh giá "Dễ" sẽ tự động đánh dấu đã học

#### ❓ Quiz
- Trả lời 10 câu hỏi trắc nghiệm
- Nhận phản hồi ngay lập tức
- Xem kết quả và điểm số
- Kết quả được lưu vào tiến độ

#### 📋 Danh sách
- Xem tất cả từ vựng
- Tìm kiếm theo từ hoặc nghĩa
- Lọc: Tất cả / Đã học / Chưa học
- Đánh dấu đã học/chưa học
- Nghe phát âm

## 🛠️ Công nghệ sử dụng

### Backend
- **Spring Boot 3.2.0** - Framework Java
- **Spring Data JPA** - ORM
- **H2 Database** - In-memory database
- **Lombok** - Giảm boilerplate code
- **Maven** - Build tool

### Frontend
- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool
- **TailwindCSS** - Styling
- **Axios** - HTTP client
- **React Icons** - Icon library

## 📝 Dữ liệu mẫu

Ứng dụng đi kèm với 20 từ vựng mẫu thuộc các cấp độ:
- **Basic**: Hello, Beautiful
- **Intermediate**: Important, Achieve, Knowledge
- **Advanced**: Magnificent, Perseverance, Eloquent
- **TOEIC**: Implement, Collaborate, Efficient
- **IELTS**: Analyze, Significant, Demonstrate

## 🎨 Tính năng nổi bật

### UI/UX
- ✅ Giao diện hiện đại với gradient và shadows
- ✅ Dark mode với transition mượt mà
- ✅ Responsive design cho mọi thiết bị
- ✅ Animations và transitions
- ✅ Loading states và error handling

### Chức năng
- ✅ Text-to-Speech cho phát âm
- ✅ Flashcard với hiệu ứng 3D flip
- ✅ Quiz với feedback real-time
- ✅ Search và filter từ vựng
- ✅ Progress tracking với streak days
- ✅ LocalStorage cho dark mode preference

## 🔧 Tùy chỉnh

### Thay đổi database
Để sử dụng MySQL/PostgreSQL thay vì H2, cập nhật `application.properties`:

```properties
# MySQL example
spring.datasource.url=jdbc:mysql://localhost:3306/vocabdb
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### Thêm từ vựng mới
Sử dụng API hoặc thêm trực tiếp vào `DataInitializer.java`

## 📄 License

MIT License - Tự do sử dụng cho mục đích cá nhân và thương mại.

## 👨‍💻 Phát triển bởi

VocabMaster - Ứng dụng học từ vựng tiếng Anh hiệu quả

---

**Chúc bạn học tốt! 📚✨**
