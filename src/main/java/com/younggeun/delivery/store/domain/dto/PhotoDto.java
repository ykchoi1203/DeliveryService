package com.younggeun.delivery.store.domain.dto;

import static com.younggeun.delivery.global.exception.type.CommonErrorCode.FILE_SAVE_ERROR;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.MenuPhoto;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.entity.StorePhoto;
import com.younggeun.delivery.store.domain.entity.StoreProfilePhoto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoDto {
  private String url;
  private String photoName;
  private Store store;
  private Menu menu;

  public PhotoDto(MenuPhoto photo) {
    this.url = photo.getUrl();
    this.photoName = photo.getPhotoName();
  }

  public void savePhotos(MultipartFile file, String localPath, String urlPath) {
    String saveFilename = "";
    String urlFilename = "";

    if (file != null) {
      String originalFilename = file.getOriginalFilename();

      String[] arrFilename = getNewSaveFile(localPath, urlPath, originalFilename);

      saveFilename = arrFilename[0];
      urlFilename = arrFilename[1];

      try {
        File newFile = new File(saveFilename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
      } catch (IOException e) {
        log.warn(e.getMessage());
        throw new RestApiException(FILE_SAVE_ERROR);
      }
    }

    this.setUrl(urlFilename);
    this.setPhotoName(saveFilename);

  }


  public StorePhoto toStorePhotoEntity() {
    return StorePhoto.builder()
        .store(store)
        .url(url)
        .photoName(photoName)
        .build();
  }

  public StoreProfilePhoto toStoreProfilePhotoEntity() {
    return StoreProfilePhoto.builder()
        .store(store)
        .url(url)
        .photoName(photoName)
        .build();
  }

  public MenuPhoto toMenuPhotoEntity() {
    return MenuPhoto.builder()
        .menu(menu)
        .url(url)
        .photoName(photoName)
        .build();
  }

  private String[] getNewSaveFile(String baseLocalPath, String baseUrlPath, String originalFilename) {
    LocalDate now = LocalDate.now();

    String[] dirs = {
        String.format("%s/%d/", baseLocalPath,now.getYear()),
        String.format("%s/%d/%02d/", baseLocalPath, now.getYear(),now.getMonthValue()),
        String.format("%s/%d/%02d/%02d/", baseLocalPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth())};

    String urlDir = String.format("%s/%d/%02d/%02d/", baseUrlPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth());

    for(String dir : dirs) {
      File file = new File(dir);
      if (!file.isDirectory()) {
        file.mkdir();
      }
    }

    String fileExtension = "";
    if (originalFilename != null) {
      int dotPos = originalFilename.lastIndexOf(".");
      if (dotPos > -1) {
        fileExtension = originalFilename.substring(dotPos + 1);
      }
    }

    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    String newFilename = String.format("%s%s", dirs[2], uuid);
    String newUrlFilename = String.format("%s%s", urlDir, uuid);
    if (fileExtension.length() > 0) {
      newFilename += "." + fileExtension;
      newUrlFilename += "." + fileExtension;
    }

    return new String[]{newFilename, newUrlFilename};
  }

}
