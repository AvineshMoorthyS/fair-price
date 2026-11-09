import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api/v1'

export interface UploadUrlResponse {
  uploadUrl: string
  bucket: string
  key: string
  expiresInSeconds: number
}

export async function getProductImageUploadUrl(
  fileName: string,
  contentType: string,
): Promise<UploadUrlResponse> {
  const { data } = await axios.get<UploadUrlResponse>(
    `${API_BASE_URL}/storage/products-images/upload-url`,
    { params: { fileName, contentType } },
  )
  return data
}

export async function uploadFileToSignedUrl(
  uploadUrl: string,
  file: File,
  contentType: string,
): Promise<void> {
  await axios.put(uploadUrl, file, {
    headers: { 'Content-Type': contentType },
  })
}

export interface SaveProductImageRequest {
  userId: number
  productName: string
  productKey: string
  contentType: string
}

export async function saveProductImage(
  request: SaveProductImageRequest,
): Promise<void> {
  await axios.post(`${API_BASE_URL}/storage/products-images`, request)
}
