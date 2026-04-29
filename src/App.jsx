import { useEffect, useState } from "react";
import AuthPage from "./AuthPage";
import { logout, apiFetch } from "./api";
import {
  BrowserRouter,
  Routes,
  Route,
  Link,
  useNavigate,
  useParams,
} from "react-router-dom";
import "./App.css";

const API = "http://localhost:8080/api";

function TopUserBar({ onLogout }) {
  return (
      <div className="top-user-bar">
        <Link className="admin-link" to="/admin/audit-logs">
          Security Logs
        </Link>

        <Link className="admin-link" to="/">
          Dashboard
        </Link>

        <span>
        Logged in as {localStorage.getItem("username")} (
          {localStorage.getItem("role")})
      </span>

        <button
            className="logout-btn"
            onClick={() => {
              logout();
              onLogout();
            }}
        >
          Logout
        </button>
      </div>
  );
}

function Dashboard() {
  const [cases, setCases] = useState([]);
  const [form, setForm] = useState({
    title: "",
    description: "",
    status: "OPEN",
  });

  const [editingId, setEditingId] = useState(null);
  const [query, setQuery] = useState("");
  const [queryType, setQueryType] = useState("General");
  const [queries, setQueries] = useState([]);

  useEffect(() => {
    loadCases();
  }, []);

  async function loadCases() {
    try {
      const data = await apiFetch("/cases");
      setCases(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error(err);
      setCases([]);
    }
  }

  async function saveCase() {
    if (!form.title.trim()) {
      alert("Case title is required");
      return;
    }

    const path = editingId ? `/cases/${editingId}` : `/cases`;
    const method = editingId ? "PUT" : "POST";

    await apiFetch(path, {
      method,
      body: JSON.stringify(form),
    });

    setForm({ title: "", description: "", status: "OPEN" });
    setEditingId(null);
    loadCases();
  }

  function editCase(c) {
    setEditingId(c.id);
    setForm({
      title: c.title || "",
      description: c.description || "",
      status: c.status || "OPEN",
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  async function deleteCase(id) {
    if (!window.confirm("Delete this case?")) return;

    await apiFetch(`/cases/${id}`, {
      method: "DELETE",
    });

    loadCases();
  }

  function generateQueries() {
    if (!query.trim()) {
      alert("Enter keyword, name, username, company, etc.");
      return;
    }

    const text = query.trim();
    const exact = `"${text}"`;

    const list = [
      {
        title: "Google Exact Match",
        query: exact,
        url: `https://www.google.com/search?q=${encodeURIComponent(exact)}`,
      },
      {
        title: "LinkedIn Profile Search",
        query: `site:linkedin.com/in ${exact}`,
        url: `https://www.google.com/search?q=${encodeURIComponent(
            `site:linkedin.com/in ${exact}`
        )}`,
      },
      {
        title: "GitHub Search",
        query: `site:github.com ${exact}`,
        url: `https://www.google.com/search?q=${encodeURIComponent(
            `site:github.com ${exact}`
        )}`,
      },
      {
        title: "PDF Documents",
        query: `${exact} filetype:pdf`,
        url: `https://www.google.com/search?q=${encodeURIComponent(
            `${exact} filetype:pdf`
        )}`,
      },
      {
        title: "DuckDuckGo",
        query: text,
        url: `https://duckduckgo.com/?q=${encodeURIComponent(text)}`,
      },
    ];

    if (queryType === "Email") {
      list.push({
        title: "Email Exposure",
        query: `${exact} email OR contact`,
        url: `https://www.google.com/search?q=${encodeURIComponent(
            `${exact} email OR contact`
        )}`,
      });
    }

    if (queryType === "Phone") {
      list.push({
        title: "Phone Exposure",
        query: `${exact} phone OR mobile`,
        url: `https://www.google.com/search?q=${encodeURIComponent(
            `${exact} phone OR mobile`
        )}`,
      });
    }

    setQueries(list);
  }

  async function copyQuery(text) {
    await navigator.clipboard.writeText(text);
    alert("Copied");
  }

  return (
      <div className="page">
        <header className="main-header">
          <h1>OSINT Investigation Dashboard</h1>
          <p>Spring Boot + React + MySQL</p>
        </header>

        <section className="card">
          <h2>{editingId ? "Edit Case" : "Create New Case"}</h2>

          <div className="case-form-row">
            <input
                placeholder="Case title"
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
            />

            <input
                placeholder="Case description"
                value={form.description}
                onChange={(e) =>
                    setForm({ ...form, description: e.target.value })
                }
            />

            <select
                value={form.status}
                onChange={(e) => setForm({ ...form, status: e.target.value })}
            >
              <option value="OPEN">OPEN</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="CLOSED">CLOSED</option>
            </select>

            <button onClick={saveCase}>
              {editingId ? "Update Case" : "Create Case"}
            </button>
          </div>
        </section>

        <section className="card">
          <h2>OSINT Query Builder</h2>

          <div className="query-row">
            <input
                placeholder="Enter keyword, name, username, company, etc."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
            />

            <select
                value={queryType}
                onChange={(e) => setQueryType(e.target.value)}
            >
              <option>General</option>
              <option>Email</option>
              <option>Phone</option>
              <option>Social</option>
            </select>

            <button onClick={generateQueries}>Generate</button>
          </div>

          {queries.length > 0 && (
              <div className="query-results">
                {queries.map((q, index) => (
                    <div className="query-box" key={index}>
                      <div>
                        <strong>{q.title}</strong>
                        <p>{q.query}</p>
                      </div>

                      <div className="query-actions">
                        <button onClick={() => copyQuery(q.query)}>Copy</button>
                        <a href={q.url} target="_blank" rel="noreferrer">
                          Search
                        </a>
                      </div>
                    </div>
                ))}
              </div>
          )}
        </section>

        <section className="card">
          <h2>Cases</h2>

          {cases.length === 0 ? (
              <p className="empty-text">No cases found.</p>
          ) : (
              <div className="table-wrap">
                <table>
                  <thead>
                  <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Status</th>
                    <th>Description</th>
                    <th>Created At</th>
                    <th>Actions</th>
                  </tr>
                  </thead>

                  <tbody>
                  {cases.map((c) => (
                      <tr key={c.id}>
                        <td>{c.id}</td>
                        <td>
                          <Link className="case-link" to={`/case/${c.id}`}>
                            {c.title}
                          </Link>
                        </td>
                        <td>
                      <span className={`status-pill ${c.status}`}>
                        {c.status}
                      </span>
                        </td>
                        <td>{c.description}</td>
                        <td>{formatDate(c.createdAt)}</td>
                        <td>
                          <div className="action-row">
                            <button
                                className="edit-btn"
                                onClick={() => editCase(c)}
                            >
                              Edit
                            </button>
                            <button
                                className="delete-btn"
                                onClick={() => deleteCase(c.id)}
                            >
                              Delete
                            </button>
                          </div>
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
          )}
        </section>
      </div>
  );
}

function CaseDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [caseData, setCaseData] = useState(null);
  const [people, setPeople] = useState([]);

  const [personForm, setPersonForm] = useState({
    firstName: "",
    lastName: "",
    fullName: "",
    company: "",
    jobTitle: "",
    location: "",
    email: "",
    phoneNumber: "",
    profilePhotoUrl: "",
    sourceUrl: "",
    sourceType: "LINKEDIN",
    collectedText: "",
    notes: "",
    confidenceScore: 1,
  });

  useEffect(() => {
    loadCase();
    loadPeople();
  }, [id]);

  async function loadCase() {
    const data = await apiFetch(`/cases/${id}`);
    setCaseData(data);
  }

  async function loadPeople() {
    const data = await apiFetch(`/person-records/case/${id}`);
    setPeople(Array.isArray(data) ? data : []);
  }

  async function savePerson() {
    const fullName =
        personForm.fullName ||
        `${personForm.firstName} ${personForm.lastName}`.trim();

    if (!fullName) {
      alert("Person name is required");
      return;
    }

    await apiFetch("/person-records", {
      method: "POST",
      body: JSON.stringify({
        ...personForm,
        caseId: Number(id),
        fullName,
      }),
    });

    setPersonForm({
      firstName: "",
      lastName: "",
      fullName: "",
      company: "",
      jobTitle: "",
      location: "",
      email: "",
      phoneNumber: "",
      profilePhotoUrl: "",
      sourceUrl: "",
      sourceType: "LINKEDIN",
      collectedText: "",
      notes: "",
      confidenceScore: 1,
    });

    loadPeople();
  }

  async function deletePerson(personId) {
    if (!window.confirm("Delete person?")) return;

    await apiFetch(`/person-records/${personId}`, {
      method: "DELETE",
    });

    loadPeople();
  }

  function exportReport() {
    window.open(`${API}/reports/case/${id}/pdf`, "_blank");
  }

  if (!caseData) {
    return (
        <div className="page">
          <p className="empty-text">Loading case...</p>
        </div>
    );
  }

  return (
      <div className="page">
        <button className="back-btn" onClick={() => navigate("/")}>
          ← Back to Dashboard
        </button>

        <section className="case-detail-card">
          <h1>{caseData.title}</h1>

          <div className="detail-grid">
            <div className="detail-box">
              <h3>Case ID</h3>
              <p>{caseData.id}</p>
            </div>

            <div className="detail-box">
              <h3>Status</h3>
              <p>{caseData.status}</p>
            </div>

            <div className="detail-box">
              <h3>Created At</h3>
              <p>{formatDate(caseData.createdAt)}</p>
            </div>
          </div>

          <div className="detail-description">
            <h3>Case Description</h3>
            <p>{caseData.description}</p>
          </div>

          <button className="wide-blue-btn" onClick={exportReport}>
            Export Investigation PDF Report
          </button>
        </section>

        <section className="card">
          <h2>Add Person Profile</h2>

          <div className="person-form">
            <input placeholder="First name" value={personForm.firstName} onChange={(e) => setPersonForm({ ...personForm, firstName: e.target.value })} />
            <input placeholder="Last name" value={personForm.lastName} onChange={(e) => setPersonForm({ ...personForm, lastName: e.target.value })} />
            <input placeholder="Full name" value={personForm.fullName} onChange={(e) => setPersonForm({ ...personForm, fullName: e.target.value })} />
            <input placeholder="Company" value={personForm.company} onChange={(e) => setPersonForm({ ...personForm, company: e.target.value })} />
            <input placeholder="Job title" value={personForm.jobTitle} onChange={(e) => setPersonForm({ ...personForm, jobTitle: e.target.value })} />
            <input placeholder="Location" value={personForm.location} onChange={(e) => setPersonForm({ ...personForm, location: e.target.value })} />
            <input placeholder="Email" value={personForm.email} onChange={(e) => setPersonForm({ ...personForm, email: e.target.value })} />
            <input placeholder="Phone number" value={personForm.phoneNumber} onChange={(e) => setPersonForm({ ...personForm, phoneNumber: e.target.value })} />
            <input placeholder="Profile photo URL" value={personForm.profilePhotoUrl} onChange={(e) => setPersonForm({ ...personForm, profilePhotoUrl: e.target.value })} />
            <input placeholder="Source URL" value={personForm.sourceUrl} onChange={(e) => setPersonForm({ ...personForm, sourceUrl: e.target.value })} />

            <select value={personForm.sourceType} onChange={(e) => setPersonForm({ ...personForm, sourceType: e.target.value })}>
              <option>LINKEDIN</option>
              <option>GITHUB</option>
              <option>GOOGLE</option>
              <option>PDF</option>
              <option>OTHER</option>
            </select>

            <input type="number" min="0" max="1" step="0.1" value={personForm.confidenceScore} onChange={(e) => setPersonForm({ ...personForm, confidenceScore: Number(e.target.value) })} />

            <textarea placeholder="Collected public text / summary" value={personForm.collectedText} onChange={(e) => setPersonForm({ ...personForm, collectedText: e.target.value })} />

            <textarea placeholder="Analyst notes" value={personForm.notes} onChange={(e) => setPersonForm({ ...personForm, notes: e.target.value })} />

            <button onClick={savePerson}>Save Person to Database</button>
          </div>
        </section>

        <section className="card">
          <h2>People Connected to This Case</h2>

          {people.length === 0 ? (
              <p className="empty-text">No people connected to this case yet.</p>
          ) : (
              <div className="people-grid">
                {people.map((person) => (
                    <div className="person-card" key={person.id}>
                      <div className="photo-box">
                        {person.profilePhotoUrl ? (
                            <img src={person.profilePhotoUrl} alt={person.fullName} />
                        ) : (
                            <span>No Photo</span>
                        )}
                      </div>

                      <h3>{person.fullName}</h3>
                      <p>{person.jobTitle}</p>
                      <p>{person.company}</p>
                      <p>{person.location}</p>

                      <div className="person-actions">
                        <Link to={`/person/${person.id}`}>View Details</Link>
                        <button onClick={() => deletePerson(person.id)}>Delete</button>
                      </div>
                    </div>
                ))}
              </div>
          )}
        </section>
      </div>
  );
}

function PersonDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [person, setPerson] = useState(null);

  useEffect(() => {
    async function load() {
      const data = await apiFetch(`/person-records/${id}`);
      setPerson(data);
    }

    load();
  }, [id]);

  if (!person) {
    return (
        <div className="page">
          <p className="empty-text">Loading person...</p>
        </div>
    );
  }

  return (
      <div className="page">
        <button className="back-btn" onClick={() => navigate(-1)}>
          ← Back
        </button>

        <section className="case-detail-card">
          <h1>{person.fullName}</h1>

          <div className="detail-grid">
            <div className="detail-box">
              <h3>Company</h3>
              <p>{person.company || "N/A"}</p>
            </div>

            <div className="detail-box">
              <h3>Job Title</h3>
              <p>{person.jobTitle || "N/A"}</p>
            </div>

            <div className="detail-box">
              <h3>Location</h3>
              <p>{person.location || "N/A"}</p>
            </div>
          </div>

          <div className="detail-description">
            <h3>Contact</h3>
            <p>Email: {person.email || "N/A"}</p>
            <p>Phone: {person.phoneNumber || "N/A"}</p>
            <p>Source: {person.sourceUrl || "N/A"}</p>
          </div>

          <div className="detail-description">
            <h3>Collected Text</h3>
            <p>{person.collectedText || "N/A"}</p>
          </div>

          <div className="detail-description">
            <h3>Analyst Notes</h3>
            <p>{person.notes || "N/A"}</p>
          </div>
        </section>
      </div>
  );
}

function AdminAuditLogs() {
  const [logs, setLogs] = useState([]);
  const [actor, setActor] = useState("");
  const [action, setAction] = useState("");
  const [page, setPage] = useState(0);

  useEffect(() => {
    loadLogs();
  }, [page]);

  async function loadLogs() {
    try {
      const params = {
        page,
        size: 25,
      };

      if (actor.trim()) params.actor = actor.trim();
      if (action.trim()) params.action = action.trim();

      const data = await apiFetch(
          `/audit-logs?page=${params.page}&size=${params.size}` +
          `${params.actor ? `&actor=${encodeURIComponent(params.actor)}` : ""}` +
          `${params.action ? `&action=${encodeURIComponent(params.action)}` : ""}`
      );

      setLogs(data.content || []);
    } catch (err) {
      console.error(err);
      setLogs([]);
    }
  }

  return (
      <div className="page">
        <section className="case-detail-card">
          <h1>Admin Security Dashboard</h1>

          <div className="detail-description">
            <h3>Audit Monitoring</h3>
            <p>
              Track authentication, MFA verification, record creation, updates,
              deletes, imports, exports, and other system activity.
            </p>
          </div>
        </section>

        <section className="card">
          <h2>Audit Log Filters</h2>

          <div className="query-row">
            <input
                placeholder="Filter by actor / username"
                value={actor}
                onChange={(e) => setActor(e.target.value)}
            />

            <select value={action} onChange={(e) => setAction(e.target.value)}>
              <option value="">All Actions</option>
              <option value="LOGIN">LOGIN</option>
              <option value="REGISTER">REGISTER</option>
              <option value="CREATE">CREATE</option>
              <option value="UPDATE">UPDATE</option>
              <option value="DELETE">DELETE</option>
              <option value="SEARCH">SEARCH</option>
              <option value="IMPORT">IMPORT</option>
              <option value="EXPORT">EXPORT</option>
            </select>

            <button
                onClick={() => {
                  setPage(0);
                  loadLogs();
                }}
            >
              Apply Filter
            </button>

            <button
                className="edit-btn"
                onClick={() => {
                  setActor("");
                  setAction("");
                  setPage(0);
                  setTimeout(loadLogs, 100);
                }}
            >
              Reset
            </button>
          </div>
        </section>

        <section className="card">
          <h2>System Audit Logs</h2>

          {logs.length === 0 ? (
              <p className="empty-text">No audit logs found.</p>
          ) : (
              <div className="table-wrap">
                <table>
                  <thead>
                  <tr>
                    <th>Time</th>
                    <th>Actor</th>
                    <th>Action</th>
                    <th>Entity</th>
                    <th>ID</th>
                    <th>Details</th>
                  </tr>
                  </thead>

                  <tbody>
                  {logs.map((log) => (
                      <tr key={log.id}>
                        <td>{formatDate(log.timestamp)}</td>
                        <td>{log.actor || "unknown"}</td>
                        <td>
                      <span className={`status-pill audit-${log.action}`}>
                        {log.action}
                      </span>
                        </td>
                        <td>{log.entityType}</td>
                        <td>{log.entityId || "-"}</td>
                        <td>{log.detail}</td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
          )}

          <div className="audit-pagination">
            <button
                disabled={page === 0}
                onClick={() => setPage(Math.max(0, page - 1))}
            >
              Previous
            </button>

            <span>Page {page + 1}</span>

            <button onClick={() => setPage(page + 1)}>Next</button>
          </div>
        </section>
      </div>
  );
}

function formatDate(value) {
  if (!value) return "";
  return String(value).replace("T", "\n");
}

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(
      !!localStorage.getItem("token")
  );

  if (!isLoggedIn) {
    return <AuthPage onAuth={() => setIsLoggedIn(true)} />;
  }

  return (
      <BrowserRouter>
        <TopUserBar onLogout={() => setIsLoggedIn(false)} />

        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/case/:id" element={<CaseDetails />} />
          <Route path="/person/:id" element={<PersonDetails />} />
          <Route path="/admin/audit-logs" element={<AdminAuditLogs />} />
        </Routes>
      </BrowserRouter>
  );
}