package com.ntou.auctionSite.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 更新使用者資訊請求
 * 所有欄位都是可選的，只更新有提供的欄位
 */
@Schema(description = "更新使用者資訊請求")
public record UpdateUserRequest(
        @Schema(description = "新的使用者名稱（可選）", example = "new_username")
        @Size(min = 3, max = 20, message = "使用者名稱長度必須在 3-20 字元之間")
        String username,

        @Schema(description = "電子郵件（可選）", example = "newemail@example.com")
        @Email(message = "電子郵件格式錯誤")
        String email,

        @Schema(description = "使用者暱稱（可選）", example = "小明")
        @Size(max = 50, message = "暱稱長度不能超過 50 字元")
        String nickname,

        @Schema(description = "聯絡地址（可選）", example = "台北市中正區羅斯福路一號")
        @Size(max = 200, message = "地址長度不能超過 200 字元")
        String address,

        @Schema(description = "聯絡電話（可選）", example = "0912345678")
        @Pattern(regexp = "^[0-9]{10}$", message = "電話號碼格式錯誤，請輸入 10 位數字")
        String phoneNumber
) {
}

