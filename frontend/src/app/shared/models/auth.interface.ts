export interface AuthRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  roles: string[];
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
}
