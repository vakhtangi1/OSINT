import { useState } from "react";
import { loginUser, registerUser, saveAuth, verifyMfa } from "./api";

export default function AuthPage({ onAuth }) {
    const [mode, setMode] = useState("login");
    const [mfaStep, setMfaStep] = useState(false);
    const [devCode, setDevCode] = useState("");

    const [form, setForm] = useState({
        username: "",
        password: "",
        role: "USER",
        code: "",
    });

    const [error, setError] = useState("");

    const update = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const submit = async (e) => {
        e.preventDefault();
        setError("");

        try {
            if (mfaStep) {
                const data = await verifyMfa({
                    username: form.username,
                    code: form.code,
                });

                saveAuth(data);
                onAuth();
                return;
            }

            if (mode === "register") {
                await registerUser({
                    username: form.username,
                    password: form.password,
                    role: form.role,
                });

                setMode("login");
                setError("");
                alert("Account created. Please login.");
                return;
            }

            const data = await loginUser({
                username: form.username,
                password: form.password,
            });

            if (data.mfaRequired) {
                setMfaStep(true);
                setDevCode(data.devMfaCode || "");
                return;
            }

            saveAuth(data);
            onAuth();
        } catch (err) {
            setError("Authentication failed. Check your details.");
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h1>OSINT Investigation Dashboard</h1>
                <p>Secure Intelligence Access</p>

                {!mfaStep && (
                    <div className="auth-tabs">
                        <button
                            type="button"
                            className={mode === "login" ? "active" : ""}
                            onClick={() => setMode("login")}
                        >
                            Login
                        </button>

                        <button
                            type="button"
                            className={mode === "register" ? "active" : ""}
                            onClick={() => setMode("register")}
                        >
                            Register
                        </button>
                    </div>
                )}

                <form onSubmit={submit}>
                    {!mfaStep ? (
                        <>
                            <input
                                name="username"
                                placeholder="Username"
                                value={form.username}
                                onChange={update}
                            />

                            <input
                                name="password"
                                type="password"
                                placeholder="Password"
                                value={form.password}
                                onChange={update}
                            />

                            {mode === "register" && (
                                <select name="role" value={form.role} onChange={update}>
                                    <option value="USER">USER</option>
                                    <option value="ADMIN">ADMIN</option>
                                </select>
                            )}
                        </>
                    ) : (
                        <>
                            <div className="mfa-box">
                                <h2>Multi-Factor Authentication</h2>
                                <p>Enter your 6-digit verification code.</p>

                                {devCode && (
                                    <div className="mfa-dev-code">
                                        Demo MFA Code: <strong>{devCode}</strong>
                                    </div>
                                )}
                            </div>

                            <input
                                name="code"
                                placeholder="6-digit MFA code"
                                value={form.code}
                                onChange={update}
                                maxLength="6"
                            />
                        </>
                    )}

                    {error && <div className="error-box">{error}</div>}

                    <button className="primary-btn" type="submit">
                        {mfaStep
                            ? "Verify MFA"
                            : mode === "login"
                                ? "Login"
                                : "Create Account"}
                    </button>

                    {mfaStep && (
                        <button
                            type="button"
                            className="secondary-btn"
                            onClick={() => {
                                setMfaStep(false);
                                setDevCode("");
                                setForm({ ...form, code: "" });
                            }}
                        >
                            Back to Login
                        </button>
                    )}
                </form>
            </div>
        </div>
    );
}