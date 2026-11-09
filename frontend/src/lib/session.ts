const SESSION_USER_ID_KEY = 'fairprice.userId'

export function getOrCreateSessionUserId(): number {
  const stored = sessionStorage.getItem(SESSION_USER_ID_KEY)
  if (stored) {
    return Number(stored)
  }

  const generated = Math.floor(Math.random() * 1_000_000_000) + 1
  sessionStorage.setItem(SESSION_USER_ID_KEY, String(generated))
  return generated
}
