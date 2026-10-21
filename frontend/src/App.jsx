
import { useState } from 'react'
import './App.css'

const API_URL = (
  import.meta.env.VITE_API_URL || 'http://localhost:8080'
).replace(/\/$/, '')

const PAGE_SIZE = 5

function App() {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('')

  const [courses, setCourses] = useState([])
  const [students, setStudents] = useState([])

  const [newCourseName, setNewCourseName] = useState('')

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [courseId, setCourseId] = useState('')

  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const [searchTerm, setSearchTerm] = useState('')
  const [page, setPage] = useState(0)
  const [sortDirection, setSortDirection] = useState('asc')

  const [editingStudent, setEditingStudent] = useState(null)
  const [editName, setEditName] = useState('')
  const [editEmail, setEditEmail] = useState('')
  const [editPhone, setEditPhone] = useState('')
  const [editCourseId, setEditCourseId] = useState('')

  async function apiRequest(path, options = {}) {
    const response = await fetch(`${API_URL}${path}`, {
      ...options,
      headers: {
        Authorization: `Basic ${btoa(`${username}:${password}`)}`,
        Accept: 'application/json',
        ...(options.body
          ? { 'Content-Type': 'application/json' }
          : {}),
        ...options.headers,
      },
    })

    if (response.status === 401) {
      throw new Error('Invalid username or password.')
    }

    if (response.status === 403) {
      throw new Error(
        'Access denied. Check your account permissions or CSRF configuration.'
      )
    }

    if (!response.ok) {
      const details = await response.text()
      throw new Error(
        `HTTP ${response.status}: ${details || response.statusText}`
      )
    }

    if (response.status === 204) return null

    const contentType = response.headers.get('content-type') || ''

    return contentType.includes('application/json')
      ? response.json()
      : null
  }

  async function refreshData() {
    const [courseData, studentData] = await Promise.all([
      apiRequest('/courses'),
      apiRequest(
        '/students?page=0&size=100&sortBy=id&direction=asc'
      ),
    ])

    setCourses(Array.isArray(courseData) ? courseData : [])

    setStudents(
      Array.isArray(studentData)
        ? studentData
        : studentData?.content ?? []
    )
  }

  async function loadData(e) {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      await refreshData()
      setPage(0)
      setMessage('Courses and students loaded successfully!')
    } catch (error) {
      setMessage(
        error instanceof TypeError
          ? 'Cannot connect to the backend. Check Spring Boot and CORS.'
          : error.message
      )
    } finally {
      setLoading(false)
    }
  }

  async function addCourse(e) {
    e.preventDefault()

    const trimmedName = newCourseName.trim()

    if (!trimmedName) {
      setMessage('Please enter a course name.')
      return
    }

    setLoading(true)
    setMessage('')

    try {
      const created = await apiRequest('/courses', {
        method: 'POST',
        body: JSON.stringify({ name: trimmedName }),
      })

      await refreshData()
      setNewCourseName('')
      setMessage(
        `Course "${created?.name ?? trimmedName}" added successfully!`
      )
    } catch (error) {
      setMessage(
        error instanceof TypeError
          ? 'Cannot connect to the backend. Check Spring Boot and CORS.'
          : error.message
      )
    } finally {
      setLoading(false)
    }
  }

  async function addStudent(e) {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const created = await apiRequest('/students', {
        method: 'POST',
        body: JSON.stringify({
          name: name.trim(),
          email: email.trim(),
          phone: phone.trim(),
          courseId: Number(courseId),
        }),
      })

      await refreshData()

      setName('')
      setEmail('')
      setPhone('')
      setCourseId('')
      setSearchTerm('')
      setPage(0)

      setMessage(
        `Student created successfully! ID: ${created?.id ?? 'created'}.`
      )
    } catch (error) {
      setMessage(
        error instanceof TypeError
          ? 'Cannot connect to the backend. Check Spring Boot and CORS.'
          : error.message
      )
    } finally {
      setLoading(false)
    }
  }

  function startEdit(student) {
    setEditingStudent(student)
    setEditName(student.name || '')
    setEditEmail(student.email || '')
    setEditPhone(student.phone || '')
    setEditCourseId(String(student.courseId ?? ''))
    setMessage('')
  }

  async function updateStudent(e) {
    e.preventDefault()

    if (!editingStudent) return

    setLoading(true)
    setMessage('')

    try {
      await apiRequest(`/students/${editingStudent.id}`, {
        method: 'PUT',
        body: JSON.stringify({
          name: editName.trim(),
          email: editEmail.trim(),
          phone: editPhone.trim(),
          courseId: Number(editCourseId),
        }),
      })

      await refreshData()

      setEditingStudent(null)
      setEditName('')
      setEditEmail('')
      setEditPhone('')
      setEditCourseId('')

      setMessage('Student updated successfully!')
    } catch (error) {
      setMessage(
        error instanceof TypeError
          ? 'Cannot connect to the backend. Check Spring Boot and CORS.'
          : error.message
      )
    } finally {
      setLoading(false)
    }
  }

  async function deleteStudent(id, studentName) {
    if (!window.confirm(`Are you sure you want to delete ${studentName}?`)) {
      return
    }

    setLoading(true)
    setMessage('')

    try {
      await apiRequest(`/students/${id}`, {
        method: 'DELETE',
      })

      await refreshData()
      setMessage(`${studentName} deleted successfully!`)
    } catch (error) {
      setMessage(
        error instanceof TypeError
          ? 'Cannot connect to the backend. Check Spring Boot and CORS.'
          : error.message
      )
    } finally {
      setLoading(false)
    }
  }

  const filteredStudents = students
    .filter((student) => {
      const query = searchTerm.trim().toLowerCase()

      if (!query) return true

      return [
        student.name,
        student.email,
        student.phone,
        student.courseName,
      ].some((value) =>
        String(value ?? '').toLowerCase().includes(query)
      )
    })
    .sort((a, b) => {
      const comparison = (a.id ?? 0) - (b.id ?? 0)
      return sortDirection === 'asc' ? comparison : -comparison
    })

  const totalPages = Math.ceil(filteredStudents.length / PAGE_SIZE)
  const currentPage = Math.min(page, Math.max(totalPages - 1, 0))

  const visibleStudents = filteredStudents.slice(
    currentPage * PAGE_SIZE,
    currentPage * PAGE_SIZE + PAGE_SIZE
  )

  return (
    <main className="container">
      <header>
        <h1>Student Management System</h1>
        <p>
          Manage students and courses using React, Spring Boot, and PostgreSQL.
        </p>
      </header>

      <section className="panel">
        <h2>Backend Login</h2>

        <form onSubmit={loadData}>
          <label htmlFor="username">Username</label>
          <input
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter backend password"
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? 'Loading...' : 'Login / Load Data'}
          </button>
        </form>
      </section>

      {message && (
        <section className="panel">
          <p className="message" role="status">
            {message}
          </p>
        </section>
      )}

      {/* Add Course: shown for the admin account */}
      {username.trim().toLowerCase() === 'admin' && (
        <section className="panel">
          <h2>Add Course</h2>

          <form onSubmit={addCourse}>
            <label htmlFor="newCourseName">Course Name</label>

            <input
              id="newCourseName"
              value={newCourseName}
              onChange={(e) => setNewCourseName(e.target.value)}
              placeholder="Enter course name"
              maxLength={100}
              required
            />

            <button type="submit" disabled={loading}>
              {loading ? 'Adding...' : 'Add Course'}
            </button>
          </form>
        </section>
      )}

      <section className="panel">
        <h2>Add Student</h2>

        <form onSubmit={addStudent}>
          <label htmlFor="studentName">Student Name</label>
          <input
            id="studentName"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter student name"
            required
          />

          <label htmlFor="studentEmail">Email</label>
          <input
            id="studentEmail"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="student@example.com"
          />

          <label htmlFor="studentPhone">Phone</label>
          <input
            id="studentPhone"
            type="tel"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="Enter phone number"
          />

          <label htmlFor="course">Select Course</label>
          <select
            id="course"
            value={courseId}
            onChange={(e) => setCourseId(e.target.value)}
            required
          >
            <option value="">Choose a course</option>
            {courses.map((course) => (
              <option key={course.id} value={course.id}>
                {course.name} (ID: {course.id})
              </option>
            ))}
          </select>

          <button
            type="submit"
            disabled={loading || courses.length === 0}
          >
            {loading ? 'Please wait...' : 'Add Student'}
          </button>
        </form>
      </section>

      {editingStudent && (
        <section className="panel">
          <h2>Edit Student - ID: {editingStudent.id}</h2>

          <form onSubmit={updateStudent}>
            <label htmlFor="editName">Student Name</label>
            <input
              id="editName"
              value={editName}
              onChange={(e) => setEditName(e.target.value)}
              required
            />

            <label htmlFor="editEmail">Email</label>
            <input
              id="editEmail"
              type="email"
              value={editEmail}
              onChange={(e) => setEditEmail(e.target.value)}
            />

            <label htmlFor="editPhone">Phone</label>
            <input
              id="editPhone"
              type="tel"
              value={editPhone}
              onChange={(e) => setEditPhone(e.target.value)}
            />

            <label htmlFor="editCourse">Select Course</label>
            <select
              id="editCourse"
              value={editCourseId}
              onChange={(e) => setEditCourseId(e.target.value)}
              required
            >
              <option value="">Choose a course</option>
              {courses.map((course) => (
                <option key={course.id} value={course.id}>
                  {course.name} (ID: {course.id})
                </option>
              ))}
            </select>

            <button type="submit" disabled={loading}>
              {loading ? 'Saving...' : 'Save Changes'}
            </button>

            <button
              type="button"
              onClick={() => setEditingStudent(null)}
              disabled={loading}
            >
              Cancel
            </button>
          </form>
        </section>
      )}

      <section className="panel">
        <h2>Courses ({courses.length})</h2>

        {courses.length === 0 ? (
          <p>Log in to load courses.</p>
        ) : (
          <div className="course-list">
            {courses.map((course) => (
              <article className="course-card" key={course.id}>
                <span className="course-id">Course ID: {course.id}</span>
                <h3>{course.name}</h3>
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="panel">
        <h2>Students ({filteredStudents.length})</h2>

        <label htmlFor="studentSearch">Search students</label>
        <input
          id="studentSearch"
          type="search"
          value={searchTerm}
          onChange={(e) => {
            setSearchTerm(e.target.value)
            setPage(0)
          }}
          placeholder="Search name, email, phone, or course..."
        />

        <label htmlFor="sortDirection">Sort by ID</label>
        <select
          id="sortDirection"
          value={sortDirection}
          onChange={(e) => {
            setSortDirection(e.target.value)
            setPage(0)
          }}
        >
          <option value="asc">Oldest first</option>
          <option value="desc">Newest first</option>
        </select>

        {students.length === 0 ? (
          <p>Log in to load students.</p>
        ) : filteredStudents.length === 0 ? (
          <p>No students match your search.</p>
        ) : (
          <>
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Course</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {visibleStudents.map((student) => (
                    <tr key={student.id}>
                      <td>{student.id}</td>
                      <td>{student.name}</td>
                      <td>{student.email || '—'}</td>
                      <td>{student.phone || '—'}</td>
                      <td>{student.courseName || '—'}</td>
                      <td>
                        <button
                          type="button"
                          onClick={() => startEdit(student)}
                          disabled={loading}
                        >
                          Edit
                        </button>

                        <button
                          type="button"
                          onClick={() =>
                            deleteStudent(student.id, student.name)
                          }
                          disabled={loading}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="pagination">
              <button
                type="button"
                disabled={currentPage === 0}
                onClick={() => setPage(currentPage - 1)}
              >
                Previous
              </button>

              <span>
                Page {currentPage + 1} of {totalPages}
              </span>

              <button
                type="button"
                disabled={currentPage >= totalPages - 1}
                onClick={() => setPage(currentPage + 1)}
              >
                Next
              </button>
            </div>
          </>
        )}
      </section>
    </main>
  )
}

export default App
