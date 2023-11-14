# DefaultApi

All URIs are relative to *https://virtserver.swaggerhub.com/IGORTON/AlbumStore/1.0.0*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAlbumByKey**](DefaultApi.md#getAlbumByKey) | **GET** /albums/{albumID} | get album by key
[**newAlbum**](DefaultApi.md#newAlbum) | **POST** /albums | Returns the new key and size of an image in bytes.

<a name="getAlbumByKey"></a>
# **getAlbumByKey**
> AlbumInfo getAlbumByKey(albumID)

get album by key

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String albumID = "albumID_example"; // String | path  parameter is album key to retrieve
try {
    AlbumInfo result = apiInstance.getAlbumByKey(albumID);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getAlbumByKey");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **albumID** | **String**| path  parameter is album key to retrieve |

### Return type

[**AlbumInfo**](AlbumInfo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="newAlbum"></a>
# **newAlbum**
> ImageMetaData newAlbum(image, profile)

Returns the new key and size of an image in bytes.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
File image = new File("image_example"); // File | 
AlbumsProfile profile = new AlbumsProfile(); // AlbumsProfile | 
try {
    ImageMetaData result = apiInstance.newAlbum(image, profile);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#newAlbum");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **image** | **File**|  |
 **profile** | [**AlbumsProfile**](.md)|  |

### Return type

[**ImageMetaData**](ImageMetaData.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

