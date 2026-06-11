import { useEffect, useState } from 'react'
import { Table, Button, Space, Modal, message, Tooltip } from 'antd'
import { PlusOutlined, DeleteOutlined, EditOutlined, StarOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { customerContactApi, type CustomerContactVO, type CustomerContactDTO } from '../../api/customer'
import ContactForm from './ContactForm'

interface ContactListProps {
  customerId: number
}

/**
 * 联系人列表组件
 */
export default function ContactList({ customerId }: ContactListProps) {
  const [contacts, setContacts] = useState<CustomerContactVO[]>([])
  const [loading, setLoading] = useState(false)
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<CustomerContactVO | undefined>(undefined)

  const loadContacts = async () => {
    setLoading(true)
    try {
      const data = await customerContactApi.listByCustomerId(customerId)
      setContacts(data)
    } catch {
      // handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (customerId) {
      loadContacts()
    }
  }, [customerId])

  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  const handleEdit = (record: CustomerContactVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定删除该联系人吗？',
      onOk: async () => {
        await customerContactApi.delete(id)
        message.success('删除成功')
        loadContacts()
      },
    })
  }

  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    loadContacts()
  }

  const columns: ColumnsType<CustomerContactVO> = [
    {
      title: '姓名',
      dataIndex: 'name',
      width: 120,
      render: (_, record) => (
        <Space>
          {record.name}
          {record.isPrimary === 1 && (
            <Tooltip title="主要联系人">
              <StarOutlined style={{ color: '#faad14' }} />
            </Tooltip>
          )}
        </Space>
      ),
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      width: 130,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      width: 180,
    },
    {
      title: '职位',
      dataIndex: 'position',
      width: 120,
    },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleEdit(record)}>
            <EditOutlined /> 编辑
          </a>
          <a onClick={() => handleDelete(record.id)} style={{ color: '#ff4d4f' }}>
            <DeleteOutlined /> 删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <strong>联系人列表</strong>
        <Button type="primary" size="small" icon={<PlusOutlined />} onClick={handleAdd}>
          新增联系人
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={contacts}
        rowKey="id"
        loading={loading}
        pagination={false}
        size="small"
      />

      <ContactForm
        open={formOpen}
        data={formData}
        customerId={customerId}
        onSuccess={handleFormSuccess}
        onCancel={() => {
          setFormOpen(false)
          setFormData(undefined)
        }}
      />
    </div>
  )
}
