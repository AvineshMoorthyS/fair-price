import { useEffect, useRef, useState } from 'react'
import toast from 'react-hot-toast'
import {
  getProductImageUploadUrl,
  saveProductImage,
  uploadFileToSignedUrl,
} from '../api/storage'
import { getOrCreateSessionUserId } from '../lib/session'

export default function ImageUploader() {
  const [productName, setProductName] = useState('')
  const [isUploading, setIsUploading] = useState(false)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl)
    }
  }, [previewUrl])

  async function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    if (!file) return

    if (!productName.trim()) {
      toast.error('Enter a product name before uploading')
      if (inputRef.current) inputRef.current.value = ''
      return
    }

    setPreviewUrl((prev) => {
      if (prev) URL.revokeObjectURL(prev)
      return URL.createObjectURL(file)
    })

    const userId = getOrCreateSessionUserId()
    const contentType = file.type || 'application/octet-stream'

    setIsUploading(true)
    try {
      const { uploadUrl, key } = await getProductImageUploadUrl(file.name, contentType)
      await uploadFileToSignedUrl(uploadUrl, file, contentType)
      await saveProductImage({
        userId,
        productName: productName.trim(),
        productKey: key,
        contentType,
      })
      toast.success('Image uploaded successfully')
    } catch (error) {
      console.error(error)
      toast.error('Failed to upload image')
    } finally {
      setIsUploading(false)
      if (inputRef.current) inputRef.current.value = ''
    }
  }

  return (
    <div className="flex flex-col items-center gap-4 p-8">
      <h1 className="text-xl font-semibold">Upload Product Image</h1>

      <div className="flex w-full max-w-sm flex-col gap-3">
        <label className="flex flex-col gap-1 text-sm text-gray-600">
          Product name
          <input
            type="text"
            value={productName}
            onChange={(e) => setProductName(e.target.value)}
            disabled={isUploading}
            placeholder="iPhone 11"
            className="rounded-md border border-gray-300 px-3 py-2 text-sm disabled:opacity-50"
          />
        </label>

        <input
          ref={inputRef}
          type="file"
          accept="image/*"
          onChange={handleFileChange}
          disabled={isUploading}
          className="block w-full text-sm text-gray-600 file:mr-4 file:rounded-md file:border-0 file:bg-indigo-600 file:px-4 file:py-2 file:text-sm file:font-medium file:text-white hover:file:bg-indigo-500 disabled:opacity-50"
        />
      </div>

      {previewUrl && (
        <img
          src={previewUrl}
          alt="Selected preview"
          className="max-h-64 max-w-sm rounded-md border border-gray-200 object-contain"
        />
      )}

      {isUploading && <p className="text-sm text-gray-500">Uploading...</p>}
    </div>
  )
}
